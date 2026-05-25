import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IncidentePayload {
  fecha_hora: string;
  descripcion: string;
  turno:         { id_turno: number };
  zona:          { id_zona: number };
  tipoIncidente: { id_tipo: number };
  severidad:     { id_severidad: number };
}

export interface Incidente extends IncidentePayload {
  id_incidente: number;
}

@Injectable({ providedIn: 'root' })
export class IncidenteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/incidentes';

  // Los reintentos por errores transitorios los maneja RetryInterceptor globalmente.
  // El interceptor solo reintenta métodos idempotentes (GET/HEAD/OPTIONS),
  // así que el POST de crearIncidente está naturalmente protegido contra duplicados.
  getIncidentes(): Observable<Incidente[]> {
    return this.http.get<Incidente[]>(this.apiUrl);
  }

  crearIncidente(payload: IncidentePayload): Observable<Incidente> {
    return this.http.post<Incidente>(this.apiUrl, payload);
  }
}
