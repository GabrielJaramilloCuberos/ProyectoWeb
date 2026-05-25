import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Observable, combineLatest, map, of, catchError, startWith, switchMap, BehaviorSubject } from 'rxjs';

import {
  AdminService,
  AuditLog, Incident, LeaderboardEntry, Shift
} from '../../services/admin.service';
import { PageHeader } from '../../../../shared/ui/page-header/page-header';
import { Modal } from '../../../../shared/ui/modal/modal';
import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { StatCard } from '../../../../shared/ui/stat-card/stat-card';
import { MOCK_AUDIT_LOGS, MOCK_INCIDENTS, MOCK_LEADERBOARD, MOCK_SHIFTS } from '../../admin.mock-data';

type Tab = 'audit' | 'incidents' | 'performance';
type RoleFilter = 'all' | 'docente' | 'coordinador' | 'administrador';

interface ReportsVM {
  auditLogs: AuditLog[];
  filteredLogs: AuditLog[];
  incidents: Incident[];
  leaderboard: LeaderboardEntry[];
  totalShifts: number;
  completedShifts: number;
  totalIncidents: number;
  resolvedIncidents: number;
  totalLogs: number;
  activeTeachers: number;
}

@Component({
  selector: 'admin-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeader, Modal, GlassCard, StatCard],
  templateUrl: './reports.html'
})
export class Reports {
  private adminService = inject(AdminService);

  activeTab: Tab = 'audit';
  showExport = false;

  private filterRoleSubject  = new BehaviorSubject<RoleFilter>('all');
  private searchQuerySubject = new BehaviorSubject<string>('');

  get filterRole(): RoleFilter { return this.filterRoleSubject.value; }
  set filterRole(v: RoleFilter) { this.filterRoleSubject.next(v); }

  get searchQuery(): string { return this.searchQuerySubject.value; }
  set searchQuery(v: string) { this.searchQuerySubject.next(v); }

  vm$: Observable<ReportsVM>;

  constructor() {
    const auditLogs$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getAuditLogs().pipe(
        catchError(() => of(MOCK_AUDIT_LOGS))
      ))
    );
    const incidents$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getIncidents().pipe(
        catchError(() => of(MOCK_INCIDENTS))
      ))
    );
    const leaderboard$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getLeaderboard().pipe(
        catchError(() => of(MOCK_LEADERBOARD))
      ))
    );
    const shifts$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getShifts().pipe(
        catchError(() => of(MOCK_SHIFTS))
      ))
    );

    this.vm$ = combineLatest([
      auditLogs$, incidents$, leaderboard$, shifts$,
      this.filterRoleSubject, this.searchQuerySubject
    ]).pipe(
      map(([auditLogs, incidents, leaderboard, shifts, role, query]) => {
        const q = query.toLowerCase();
        const filteredLogs = auditLogs.filter(log => {
          if (role !== 'all' && log.role.toLowerCase() !== role) return false;
          if (q &&
              !log.action.toLowerCase().includes(q) &&
              !log.details.toLowerCase().includes(q) &&
              !log.user.toLowerCase().includes(q)) return false;
          return true;
        });

        return {
          auditLogs,
          filteredLogs,
          incidents,
          leaderboard,
          totalShifts:       shifts.length,
          completedShifts:   shifts.filter(s => s.status === 'completed').length,
          totalIncidents:    incidents.length,
          resolvedIncidents: incidents.filter(i => i.resolved).length,
          totalLogs:         auditLogs.length,
          activeTeachers:    leaderboard.length
        };
      })
    );
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
  }

  severityClass(sev: string): string {
    return {
      S1: 'bg-yellow-100 text-yellow-700',
      S2: 'bg-orange-100 text-orange-700',
      S3: 'bg-red-100 text-red-700'
    }[sev] ?? 'bg-gray-100 text-gray-700';
  }

  typeLabel(type: string): string {
    return {
      physical:    'Seguridad Física',
      coexistence: 'Convivencia',
      space:       'Uso del Espacio',
      social:      'Social'
    }[type] ?? type;
  }

  actionIconColor(action: string): string {
    if (action.includes('completado') || action.includes('resuelto')) return 'text-green-600 bg-green-100';
    if (action.includes('reportado')) return 'text-orange-600 bg-orange-100';
    if (action.includes('reasignado')) return 'text-blue-600 bg-blue-100';
    return 'text-gray-600 bg-gray-100';
  }

  actionIcon(action: string): 'calendar' | 'alert' | 'file' | 'clock' {
    if (action.includes('Turno')) return 'calendar';
    if (action.includes('Incidente')) return 'alert';
    if (action.includes('Zona') || action.includes('Configuración')) return 'file';
    return 'clock';
  }

  rankClass(index: number): string {
    if (index === 0) return 'bg-gradient-to-br from-amber-400 to-yellow-500 text-white';
    if (index === 1) return 'bg-gradient-to-br from-slate-400 to-slate-500 text-white';
    if (index === 2) return 'bg-gradient-to-br from-orange-400 to-orange-500 text-white';
    return 'bg-gray-100 text-gray-600';
  }

  punctualityClass(rate: number): string {
    if (rate >= 95) return 'bg-green-100 text-green-700';
    if (rate >= 85) return 'bg-blue-100 text-blue-700';
    return 'bg-yellow-100 text-yellow-700';
  }

  exportAs(format: 'pdf' | 'csv' | 'json'): void {
    alert(`Reporte exportado en formato ${format.toUpperCase()}`);
    this.showExport = false;
  }

  formatDate(iso: string): string {
    const d = new Date(iso);
    const months = ['Ene','Feb','Mar','Abr','May','Jun','Jul','Ago','Sep','Oct','Nov','Dic'];
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${pad(d.getDate())} ${months[d.getMonth()]}, ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }
}