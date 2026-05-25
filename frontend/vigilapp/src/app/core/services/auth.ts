import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError, timeout } from 'rxjs';
import { LoginResponse } from '../models/user/login-response';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private api = 'http://localhost:8080/auth';

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/login`, { email, password }).pipe(
      timeout(15000),
      catchError((error) => {
        if (error?.name === 'TimeoutError') {
          return throwError(() => ({ error: { message: 'Tiempo de espera agotado. Intenta de nuevo.' } }));
        }
        return throwError(() => error);
      })
    );
  }

  getToken(): string | null {
    if (!this.isBrowser()) return null;
    return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY);
  }

  getRole(): string | null {
    const user = this.getUser();
    return user?.role || null;
  }

  getUser(): { username: string; role: string } | null {
    if (!this.isBrowser()) return null;
    const raw = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  setUser(response: LoginResponse, rememberMe: boolean): void {
    if (!this.isBrowser()) return;
    const storage = rememberMe ? localStorage : sessionStorage;
    storage.setItem(TOKEN_KEY, response.token);
    storage.setItem(USER_KEY, JSON.stringify({ username: response.username, role: response.role }));
  }

  logout(): void {
    if (!this.isBrowser()) return;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}