import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  private static readonly API_BASE = 'https://vigilapp-backend.onrender.com';
  private apiUrl = IncidenteService.API_BASE + '/api/incidentes';

  constructor(private http: HttpClient) {}

  getIncidentes(): Observable<Incidente[]> {
    return this.http.get<Incidente[]>(this.apiUrl);
  }

  crearIncidente(payload: any): Observable<Incidente> {
    return this.http.post<Incidente>(this.apiUrl, payload);
  }
}
