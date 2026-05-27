import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Checkpoint {
  id_checkpoint: number;
  nombre: string;
  descripcion: string;
  zona: { id_zona: number };
  activo: boolean;
}

@Injectable({ providedIn: 'root' })
export class CheckpointService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/api/checkpoints';

  getCheckpoints(): Observable<Checkpoint[]> {
    return this.http.get<Checkpoint[]>(this.apiUrl);
  }

  getCheckpointsByZona(idZona: number): Observable<Checkpoint[]> {
    return this.http.get<Checkpoint[]>(`${this.apiUrl}?zona=${idZona}`);
  }
}
