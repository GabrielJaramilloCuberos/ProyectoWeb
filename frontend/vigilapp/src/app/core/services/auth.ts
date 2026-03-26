import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rol } from '../models/rol';
import { UsuarioLogin } from '../models/user/user-login';


@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private api = 'https://vigilapp-backend.onrender.com';

  login(email: string, password: string): Observable<UsuarioLogin> {
    return this.http.post<UsuarioLogin>(`${this.api}/api/usuarios/login`, { email, password });
  }
}