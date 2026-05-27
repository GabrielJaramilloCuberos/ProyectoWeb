import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Auth } from './auth';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private apiUrl = environment.apiUrl + '/api/usuarios';
  private auth = inject(Auth);

  constructor(private http: HttpClient) {}

  getUsuarioActual(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/actual`);
  }

  /**
   * Delega en Auth.logout() para asegurar que se limpian tanto localStorage
   * como sessionStorage. Antes solo hacía localStorage.clear(), lo cual dejaba
   * el token vivo si el usuario no había marcado "Recordarme" (porque en ese
   * caso el token se guarda en sessionStorage, no en localStorage).
   */
  logout() {
    this.auth.logout();
  }
}
