import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Zona {
  id_zona: number;
  nombre: string;
  descripcion: string;
  tipo: string;
  activa: boolean;
}

@Injectable({ providedIn: 'root' })
export class ZonaService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/api/zonas';

  // Los reintentos por errores transitorios los maneja RetryInterceptor globalmente.
  getZonas(): Observable<Zona[]> {
    return this.http.get<Zona[]>(this.apiUrl);
  }
}
