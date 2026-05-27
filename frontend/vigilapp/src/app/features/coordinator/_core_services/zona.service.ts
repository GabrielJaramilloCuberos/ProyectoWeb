import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Zona {
  id_zona:     number;
  nombre:      string;
  descripcion: string;
  tipo:        string;
  activa:      boolean;
}

@Injectable({ providedIn: 'root' })
export class ZonaService {
  private static readonly API_BASE = 'https://vigilapp-backend.onrender.com';
  private apiUrl = ZonaService.API_BASE + '/api/zonas';

  constructor(private http: HttpClient) {}

  getZonas(): Observable<Zona[]> {
    return this.http.get<Zona[]>(this.apiUrl);
  }
}
