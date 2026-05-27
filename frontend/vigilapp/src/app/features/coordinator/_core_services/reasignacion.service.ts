import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReasignacionPayload {
  motivo:          string;
  fecha_propuesta: string;
  estado:          string;
  turno:            { id_turno:   number };
  docenteOriginal:  { id_usuario: number };
  docentePropuesto: { id_usuario: number };
}

export interface Reasignacion extends ReasignacionPayload {
  id_reasignacion: number;
  fecha_respuesta?: string;
}

@Injectable({ providedIn: 'root' })
export class ReasignacionService {
  private static readonly API_BASE = 'https://vigilapp-backend.onrender.com';
  private apiUrl = ReasignacionService.API_BASE + '/api/reasignaciones';

  constructor(private http: HttpClient) {}

  create(payload: ReasignacionPayload): Observable<Reasignacion> {
    return this.http.post<Reasignacion>(this.apiUrl, payload);
  }
}
