import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface IncidentePayload {
  fecha_hora: string;
  descripcion: string;
  turno:         { id_turno: number };
  zona:          { id_zona: number };
  tipoIncidente: { id_tipo: number };
  severidad:     { id_severidad: number };
}

export interface Incidente {
  id_incidente: number;
  fecha_hora:   string;
  descripcion:  string;
  turno?:       { id_turno: number; docente?: any; zona?: any };
  zona?:        { id_zona: number; nombre?: string };
  severidad?:   { id_severidad: number; codigo?: string };
  tipoIncidente?: { id_tipo: number; nombre?: string };
}

@Injectable({ providedIn: 'root' })
export class IncidenteService {
  private http   = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/api/incidentes';

  getIncidentes(): Observable<Incidente[]> {
    return this.http.get<Incidente[]>(this.apiUrl);
  }

  crearIncidente(payload: IncidentePayload): Observable<Incidente> {
    return this.http.post<Incidente>(this.apiUrl, payload);
  }
}