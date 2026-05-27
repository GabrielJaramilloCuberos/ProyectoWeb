import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {

  private apiUrl = environment.apiUrl + '/api/notificaciones';

  constructor(private http: HttpClient) {}

  getNotificaciones(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}