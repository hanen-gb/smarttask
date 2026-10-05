package com.smarttask.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttask.backend.auth.dto.AuthResponse;
import com.smarttask.backend.auth.dto.RegisterRequest;
import com.smarttask.backend.security.CustomUserDetailsService;
import com.smarttask.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de la couche WEB uniquement : @WebMvcTest ne demarre QUE l'infrastructure
 * MVC (le Controller, la serialisation JSON, la validation) -- pas la base de
 * donnees, pas le vrai AuthService. On verifie le contrat HTTP (codes de statut,
 * forme du JSON), pas la logique metier (deja testee ailleurs).
 *
 * addFilters = false : desactive les filtres de securite pour ce test -- on ne
 * teste pas l'authentification ici, juste le Controller lui-meme.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Construit directement (pas @Autowired) : ce slice de test ne fournit pas
    // le bean ObjectMapper configure par Spring -- un ObjectMapper basique
    // suffit amplement pour serialiser nos simples DTO dans ce test.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // @MockitoBean : remplace AuthService par un faux dans le contexte Spring de ce test
    @MockitoBean
    private AuthService authService;

    // JwtAuthenticationFilter est un @Component de type Filter -- @WebMvcTest
    // l'inclut automatiquement dans son contexte (meme desactive par addFilters
    // = false, Spring doit quand meme construire le bean). Il lui faut donc ses
    // propres dependances, qu'on simule ici sans jamais les utiliser reellement.
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_avecDonneesValides_renvoie201() throws Exception {
        var request = new RegisterRequest("test@smarttask.io", "Password123", "Test", "User");
        var response = new AuthResponse(
                "faux-token", null, "Bearer", 900L,
                new AuthResponse.UserSummary("id-1", "test@smarttask.io", "Test", "User", "MEMBER")
        );

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("faux-token"))
                .andExpect(jsonPath("$.user.email").value("test@smarttask.io"));
    }

    @Test
    void register_avecEmailInvalide_renvoie400SansAppelerLeService() throws Exception {
        // "pas-un-email" viole @Email sur RegisterRequest -- @Valid doit bloquer
        // AVANT que le controller n'appelle authService.
        var request = new RegisterRequest("pas-un-email", "Password123", "Test", "User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_avecMotDePasseTropCourt_renvoie400() throws Exception {
        var request = new RegisterRequest("test@smarttask.io", "court1A", "Test", "User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
