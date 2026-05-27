import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ReasignacionPayload {
  motivo: string;
  fecha_propuesta: string;  // ISO-8601, e.g. "2026-05-25T10:00:00"
  fecha_respuesta?: string; // ISO-8601, opcional (presente cuando estado es APROBADA/RECHAZADA)
  estado: string;           // "PENDIENTE" | "APROBADA" | "RECHAZADA"
  turno:            { id_turno: number };
  docenteOriginal:  { id_usuario: number };
  docentePropuesto: { id_usuario: number };
}

export interface Reasignacion extends ReasignacionPayload {
  id_reasignacion: number;
}

@Injectable({ providedIn: 'root' })
export class ReasignacionService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/api/reasignaciones';

  getAll(): Observable<Reasignacion[]> {
    return this.http.get<Reasignacion[]>(this.apiUrl);
  }

  getById(id: number): Observable<Reasignacion> {
    return this.http.get<Reasignacion>(`${this.apiUrl}/${id}`);
  }

  create(payload: ReasignacionPayload): Observable<Reasignacion> {
    return this.http.post<Reasignacion>(this.apiUrl, payload);
  }

  update(id: number, payload: ReasignacionPayload): Observable<Reasignacion> {
    return this.http.put<Reasignacion>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
