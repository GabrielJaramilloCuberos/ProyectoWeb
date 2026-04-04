import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError, timeout } from 'rxjs';
import { UsuarioLogin } from '../models/user/user-login';


@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private api = 'http://localhost:8080';

  login(email: string, password: string): Observable<UsuarioLogin> {
    return this.http.post<UsuarioLogin>(`${this.api}/auth/login`, { email, password }).pipe(
      timeout(15000),
      catchError((error) => {
        if (error?.name === 'TimeoutError') {
          return throwError(() => ({ error: { message: 'Tiempo de espera agotado. Intenta de nuevo.' } }));
        }
        return throwError(() => error);
      })
    );
  }
}