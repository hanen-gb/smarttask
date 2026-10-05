import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { TokenService } from './token.service';
import { environment } from '../../../environments/environment';

/**
 * Test avec TestBed + HttpTestingController : AuthService est instancie pour
 * de vrai (pas mocke), mais ses requetes HTTP sont interceptees -- aucune
 * vraie requete reseau ne part. On controle nous-memes la reponse simulee
 * avec req.flush(...), et on verifie l'effet sur le signal currentUser.
 */
describe('AuthService', () => {
  let httpMock: HttpTestingController;
  let tokenService: TokenService;

  beforeEach(() => {
    // Evite qu'un token laisse par un test precedent ne declenche
    // fetchCurrentUser() au constructeur du test suivant.
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: { navigateByUrl: () => {} } },
      ],
    });

    httpMock = TestBed.inject(HttpTestingController);
    tokenService = TestBed.inject(TokenService);
  });

  afterEach(() => {
    // Echoue le test si une requete HTTP a ete faite sans etre verifiee
    httpMock.verify();
  });

  it('login() stocke le token et met a jour currentUser', () => {
    const service = TestBed.inject(AuthService);

    const fakeResponse = {
      accessToken: 'faux-token',
      refreshToken: null,
      tokenType: 'Bearer',
      expiresIn: 900,
      user: { id: '1', email: 'test@smarttask.io', firstName: 'Test', lastName: 'User', role: 'MEMBER' as const },
    };

    service.login({ email: 'test@smarttask.io', password: 'Password123' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(fakeResponse);

    expect(tokenService.getToken()).toBe('faux-token');
    expect(service.currentUser()).toEqual(fakeResponse.user);
    expect(service.isAuthenticated()).toBe(true);
  });

  it('logout() efface le token et remet currentUser a null', () => {
    const service = TestBed.inject(AuthService);
    tokenService.setToken('ancien-token');

    service.logout();

    expect(tokenService.getToken()).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
  });

  it("au demarrage, si un token existe deja, appelle /auth/me pour repeupler currentUser", () => {
    tokenService.setToken('token-existant');

    // Le constructeur d'AuthService declenche fetchCurrentUser() DES l'injection,
    // car tokenService.getToken() renvoie deja une valeur ici.
    const service = TestBed.inject(AuthService);

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/me`);
    req.flush({ id: '1', email: 'test@smarttask.io', firstName: 'Test', lastName: 'User', role: 'MEMBER' });

    expect(service.currentUser()?.email).toBe('test@smarttask.io');
  });
});
