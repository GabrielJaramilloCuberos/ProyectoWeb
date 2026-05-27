import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TurnoBackend {
  id_turno:              number;
  fecha:                 string;
  hora_inicio:           string;
  hora_fin:              string;
  estado:                string;
  limpieza_calificacion: number;
  docente: {
    id_usuario: number;
    nombre:     string;
    email:      string;
    estado:     boolean;
    rol:        { id_rol: number; nombre: string };
  };
  zona: {
    id_zona:     number;
    nombre:      string;
    descripcion: string;
    tipo:        string;
    activa:      boolean;
  };
}

@Injectable({ providedIn: 'root' })
export class TurnoService {
  private static readonly API_BASE = 'https://vigilapp-backend.onrender.com';
  private apiUrl = TurnoService.API_BASE + '/api/turnos';

  constructor(private http: HttpClient) {}

  getTurnos(): Observable<TurnoBackend[]> {
    return this.http.get<TurnoBackend[]>(this.apiUrl);
  }

  tomarTurno(idTurno: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${idTurno}/tomar`, {});
  }

  reasignarTurno(idTurno: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${idTurno}/reasignar`, {});
  }
}
