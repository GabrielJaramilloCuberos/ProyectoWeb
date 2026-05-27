import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, of } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Zone {
  id: string;
  name: string;
  description: string;
  checkpoints: string[];
  isActive: boolean;
}

export interface Teacher {
  id: string;
  name: string;
  department: string;
  isActive: boolean;
}

export type ShiftStatus = 'pending' | 'active' | 'completed' | 'missed';

export interface Shift {
  id: string;
  teacherId: string;
  teacherName: string;
  zoneId: string;
  zoneName: string;
  date: string;
  startTime: string;
  endTime: string;
  status: ShiftStatus;
  checkInTime?: string;
}

export interface SystemConfig {
  shiftDuration: number;
  minPatrols: number;
  notificationMinutes: number;
  autoReassignMinutes: number;
  gamificationEnabled: boolean;
  pointsPerShift: number;
  pointsPerPatrol: number;
  pointsPerReport: number;
}

export interface AuditLog {
  id: string;
  action: string;
  details: string;
  role: 'Docente' | 'Coordinador' | 'Administrador';
  user: string;
  timestamp: string;
}

export interface Incident {
  id: string;
  severity: 'S1' | 'S2' | 'S3';
  type: 'physical' | 'coexistence' | 'space' | 'social';
  description: string;
  zoneName: string;
  teacherName: string;
  resolved: boolean;
  timestamp: string;
}

export interface LeaderboardEntry {
  teacherId: string;
  teacherName: string;
  department: string;
  points: number;
  shiftsCompleted: number;
  punctualityRate: number;
  patrolsTotal: number;
  incidentsReported: number;
  badges: number;
}

export const DEFAULT_CONFIG: SystemConfig = {
  shiftDuration: 30,
  minPatrols: 2,
  notificationMinutes: 15,
  autoReassignMinutes: 10,
  gamificationEnabled: true,
  pointsPerShift: 100,
  pointsPerPatrol: 25,
  pointsPerReport: 50
};

@Injectable({ providedIn: 'root' })
export class AdminService {

  private platformId = inject(PLATFORM_ID);
  private apiUrl = environment.apiUrl + '/api/admin';

  private refreshSubject = new Subject<void>();
  readonly refresh$: Observable<void> = this.refreshSubject.asObservable();

  constructor(private http: HttpClient) {}

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  notifyChange(): void {
    this.refreshSubject.next();
  }

  /* ── Zones ── */
  getZones(): Observable<Zone[]> {
    if (!this.isBrowser()) return of([]);
    return this.http.get<Zone[]>(`${this.apiUrl}/zones`);
  }
  addZone(zone: Omit<Zone, 'id'>): Observable<Zone> {
    return this.http.post<Zone>(`${this.apiUrl}/zones`, zone);
  }
  updateZone(id: string, patch: Partial<Zone>): Observable<Zone> {
    return this.http.patch<Zone>(`${this.apiUrl}/zones/${id}`, patch);
  }
  deleteZone(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/zones/${id}`);
  }

  /* ── Teachers ── */
  getTeachers(): Observable<Teacher[]> {
    if (!this.isBrowser()) return of([]);
    return this.http.get<Teacher[]>(`${this.apiUrl}/teachers`);
  }

  /* ── Shifts ── */
  getShifts(): Observable<Shift[]> {
    if (!this.isBrowser()) return of([]);
    return this.http.get<Shift[]>(`${this.apiUrl}/shifts`);
  }
  addShift(shift: Omit<Shift, 'id'>): Observable<Shift> {
    return this.http.post<Shift>(`${this.apiUrl}/shifts`, shift);
  }
  deleteShift(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/shifts/${id}`);
  }

  /* ── Config ── */
  getConfig(): Observable<SystemConfig> {
    if (!this.isBrowser()) return of({ ...DEFAULT_CONFIG });
    return this.http.get<SystemConfig>(`${this.apiUrl}/config`);
  }
  updateConfig(config: SystemConfig): Observable<SystemConfig> {
    return this.http.put<SystemConfig>(`${this.apiUrl}/config`, config);
  }
  resetConfig(): Observable<SystemConfig> {
    return this.http.post<SystemConfig>(`${this.apiUrl}/config/reset`, {});
  }

  /* ── Reports ── */
  getAuditLogs(): Observable<AuditLog[]> {
    if (!this.isBrowser()) return of([]);
    return this.http.get<AuditLog[]>(`${this.apiUrl}/audit-logs`);
  }
  getIncidents(): Observable<Incident[]> {
    if (!this.isBrowser()) return of([]);
    return this.http.get<Incident[]>(`${this.apiUrl}/incidents`);
  }
  getLeaderboard(): Observable<LeaderboardEntry[]> {
    if (!this.isBrowser()) return of([]);
    return this.http.get<LeaderboardEntry[]>(`${this.apiUrl}/leaderboard`);
  }
}