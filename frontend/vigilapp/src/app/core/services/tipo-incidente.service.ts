import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TipoIncidente {
  id_tipo: number;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class TipoIncidenteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/tipos-incidente';

  // Los reintentos por errores transitorios los maneja RetryInterceptor globalmente.
  getTipos(): Observable<TipoIncidente[]> {
    return this.http.get<TipoIncidente[]>(this.apiUrl);
  }
}
