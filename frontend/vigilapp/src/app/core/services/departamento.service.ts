import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Departamento {
  id_departamento: number;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class DepartamentoService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/api/departamentos';

  // Los reintentos por errores transitorios los maneja RetryInterceptor globalmente.
  getDepartamentos(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(this.apiUrl);
  }
}
