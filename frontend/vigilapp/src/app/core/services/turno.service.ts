import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TurnoBackend {
  id_turno:               number;
  fecha:                  string;       // "2026-05-25"
  hora_inicio:            string;       // "07:00:00"
  hora_fin:               string;       // "09:00:00"
  estado:                 string;       // "ACTIVO" | "COMPLETADO" | "AUSENTE" | "PENDIENTE" | "DISPONIBLE" | "ASIGNADO"
  limpieza_calificacion:  number;
  docente: {
    id_usuario: number;
    nombre:     string;
    email:      string;
    estado:     boolean;
    rol: { id_rol: number; nombre: string };
  };
  zona: {
    id_zona:     number;
    nombre:      string;
    descripcion: string;
    tipo:        string;
    activa:      boolean;
  };
}

export interface TurnoPayload {
  fecha:                 string;
  hora_inicio:           string;
  hora_fin:              string;
  estado:                string;
  limpieza_calificacion: number;
  docente: { id_usuario: number };
  zona:    { id_zona:    number };
}

@Injectable({ providedIn: 'root' })
export class TurnoService {
  private http   = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/turnos';

  getTurnos(): Observable<TurnoBackend[]> {
    return this.http.get<TurnoBackend[]>(this.apiUrl);
  }

  getById(id: number): Observable<TurnoBackend> {
    return this.http.get<TurnoBackend>(`${this.apiUrl}/${id}`);
  }

  create(payload: TurnoPayload): Observable<TurnoBackend> {
    return this.http.post<TurnoBackend>(this.apiUrl, payload);
  }

  update(id: number, payload: TurnoPayload): Observable<TurnoBackend> {
    return this.http.put<TurnoBackend>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  tomarTurno(id: number): Observable<TurnoBackend> {
    return this.http.patch<TurnoBackend>(`${this.apiUrl}/${id}/tomar`, {});
  }

  reasignarTurno(id: number): Observable<TurnoBackend> {
    return this.http.patch<TurnoBackend>(`${this.apiUrl}/${id}/reasignar`, {});
  }
}