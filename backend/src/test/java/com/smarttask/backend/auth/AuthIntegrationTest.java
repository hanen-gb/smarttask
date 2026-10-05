package com.smarttask.backend.auth;

import com.smarttask.backend.auth.dto.AuthResponse;
import com.smarttask.backend.auth.dto.LoginRequest;
import com.smarttask.backend.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test D'INTEGRATION : demarre TOUTE l'application Spring (vrai contexte, vrai
 * Flyway, vraie securite) contre un VRAI PostgreSQL -- mais jetable, cree dans
 * un conteneur Docker rien que pour ce test, detruit a la fin.
 *
 * Avantage sur une base H2 en memoire : on teste EXACTEMENT le meme moteur de
 * base de donnees qu'en production (meme dialecte SQL, memes contraintes).
 *
 * Necessite Docker Desktop demarre sur la machine qui execute les tests.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class AuthIntegrationTest {

    @Container
    @ServiceConnection // Spring reconfigure AUTOMATIQUEMENT le datasource vers ce conteneur
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void inscriptionPuisConnexion_parcoursComplet_fonctionne() {
        var registerRequest = new RegisterRequest(
                "integration@smarttask.io", "Password123", "Integration", "Test");

        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                "/api/auth/register", registerRequest, AuthResponse.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().accessToken()).isNotBlank();
        // Le mot de passe en clair ne doit JAMAIS apparaitre dans la reponse
        assertThat(registerResponse.getBody().toString()).doesNotContain("Password123");

        var loginRequest = new LoginRequest("integration@smarttask.io", "Password123");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void inscription_emailDejaUtilise_renvoie409() {
        var request = new RegisterRequest("duplicate@smarttask.io", "Password123", "Dup", "User");
        restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);

        ResponseEntity<String> second = restTemplate.postForEntity("/api/auth/register", request, String.class);

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void connexion_mauvaisMotDePasse_renvoieErreur() {
        restTemplate.postForEntity("/api/auth/register",
                new RegisterRequest("wrongpass@smarttask.io", "Password123", "Test", "User"),
                AuthResponse.class);

        var badLogin = new LoginRequest("wrongpass@smarttask.io", "MauvaisMotDePasse1");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", badLogin, String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void routeProtegee_sansToken_estRefusee() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/projects", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
