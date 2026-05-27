import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MetricasService {

  private apiUrl = environment.apiUrl + '/api/metricas-docente';

  constructor(private http: HttpClient) {}

  getMetricasDocente(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
}