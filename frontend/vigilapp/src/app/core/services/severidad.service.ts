import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Severidad {
  id_severidad: number;
  codigo: string;
  descripcion: string;
}

@Injectable({ providedIn: 'root' })
export class SeveridadService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/severidades';

  // Los reintentos por errores transitorios los maneja RetryInterceptor globalmente.
  getSeveridades(): Observable<Severidad[]> {
    return this.http.get<Severidad[]>(this.apiUrl);
  }
}
