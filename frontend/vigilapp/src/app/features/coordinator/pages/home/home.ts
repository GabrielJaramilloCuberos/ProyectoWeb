import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, OnDestroy, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { CoordinatorActiveShifts } from './components/coordinator-active-shifts/coordinator-active-shifts';
import { CoordinatorActivityFeed } from './components/coordinator-activity-feed/coordinator-activity-feed';
import { CoordinatorAlerts } from './components/coordinator-alerts/coordinator-alerts';
import { CoordinatorHeader } from './components/coordinator-header/coordinator-header';
import { HeatmapComponent } from './components/heatmap/heatmap.component';
import { CoordinatorReassignDialog } from './components/coordinator-reassign-dialog/coordinator-reassign-dialog';
import { CoordinatorShiftDetailsDialog } from './components/coordinator-shift-details-dialog/coordinator-shift-details-dialog';
import { CoordinatorStats } from './components/coordinator-stats/coordinator-stats';
import { CoordinatorZoneDetailsDialog } from './components/coordinator-zone-details-dialog/coordinator-zone-details-dialog';

import { CoordinatorService } from '../../coordinator.service';
import { ReasignacionService } from '../../../../core/services/reasignacion.service';
import { Auth } from '../../../../core/services/auth';

import {
  CoordinatorIncident,
  CoordinatorShift,
  CoordinatorTeacher,
  CoordinatorZone,
} from './coordinator-home.models';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    CoordinatorHeader,
    CoordinatorStats,
    CoordinatorAlerts,
    HeatmapComponent,
    CoordinatorActiveShifts,
    CoordinatorActivityFeed,
    CoordinatorShiftDetailsDialog,
    CoordinatorZoneDetailsDialog,
    CoordinatorReassignDialog,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  private platformId          = inject(PLATFORM_ID);
  private readonly router     = inject(Router);
  private readonly coordSvc   = inject(CoordinatorService);
  private readonly reasignSvc = inject(ReasignacionService);
  private readonly auth       = inject(Auth);

  // ── Estado de carga ──────────────────────────────────────────────────────
  protected loading = true;
  protected error: string | null = null;

  // ── Datos del dashboard ──────────────────────────────────────────────────
  protected today     = new Date().toISOString().slice(0, 10);
  protected shifts:    CoordinatorShift[]   = [];
  protected incidents: CoordinatorIncident[] = [];
  protected zones:     CoordinatorZone[]    = [];
  protected teachers:  CoordinatorTeacher[] = [];

  // ── Estado UI ────────────────────────────────────────────────────────────
  protected autoRefresh    = true;
  protected selectedShift: CoordinatorShift | null = null;
  protected selectedZoneId: string | null = null;
  protected reassignShift: CoordinatorShift | null = null;
  protected reassigning    = false;
  protected reassignError: string | null = null;

  private refreshHandle: number | null = null;
  private sub: Subscription | null = null;

  // ── Ciclo de vida ────────────────────────────────────────────────────────

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    const user = this.auth.getUser();
    if (!user) {
      void this.router.navigateByUrl('/login');
      return;
    }

    // El backend devuelve 'COORDINADOR' (mayúsculas)
    const role = (user.role ?? '').replace('ROLE_', '').toUpperCase();
    if (role !== 'COORDINADOR') {
      void this.router.navigateByUrl('/login');
      return;
    }

    this.loadData();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  // ── Carga de datos ───────────────────────────────────────────────────────

  private loadData(): void {
    this.sub?.unsubscribe();
    
    this.error   = null;
    console.log('🟡 loadData iniciado');

    this.sub = this.coordSvc.loadDashboard().subscribe({
      next: (data) => {
        console.log('✅ next llamado', data);
        this.today     = data.today;
        this.shifts    = data.shifts;
        this.incidents = data.incidents;
        this.zones     = data.zones;
        this.teachers  = data.teachers;
        this.loading   = false;
      },
      error: (err: any) => {
        console.error('❌ error llamado', err);
        this.error   = 'No se pudo conectar con el servidor.';
        this.loading = false;
      },
    });
  }

  // ── Getters derivados ────────────────────────────────────────────────────

  get todayShifts(): CoordinatorShift[] {
    return this.shifts.filter(s => s.date === this.today);
  }

  get activeShifts(): CoordinatorShift[] {
    return this.todayShifts.filter(s => s.status === 'active');
  }

  get missedShifts(): CoordinatorShift[] {
    return this.todayShifts.filter(s => s.status === 'missed');
  }

  get todayIncidents(): CoordinatorIncident[] {
    return this.incidents.filter(i => i.timestamp.startsWith(this.today));
  }

  get patrolCount(): number {
    return this.activeShifts.reduce((sum, s) => sum + (s.patrolCount ?? 0), 0);
  }

  get availableTeachers(): CoordinatorTeacher[] {
    return this.teachers.filter(t => t.isActive);
  }

  get selectedZoneData(): CoordinatorZone | null {
    return this.selectedZoneId
      ? (this.zones.find(z => z.id === this.selectedZoneId) ?? null)
      : null;
  }

  get selectedZoneShifts(): CoordinatorShift[] {
    return this.selectedZoneId
      ? this.todayShifts.filter(s => s.zoneId === this.selectedZoneId)
      : [];
  }

  get selectedZoneIncidents(): CoordinatorIncident[] {
    return this.selectedZoneId
      ? this.todayIncidents.filter(i => i.zoneId === this.selectedZoneId)
      : [];
  }

  get selectedZoneIncidentSummary(): { total: number; lastMonth: number; lastWeek: number } {
    if (!this.selectedZoneId) return { total: 0, lastMonth: 0, lastWeek: 0 };
    const all      = this.incidents.filter(i => i.zoneId === this.selectedZoneId);
    const ref      = new Date(`${this.today}T23:59:59`);
    const weekAgo  = new Date(ref); weekAgo.setDate(ref.getDate() - 7);
    const monthAgo = new Date(ref); monthAgo.setDate(ref.getDate() - 30);
    return {
      total:     all.length,
      lastMonth: all.filter(i => new Date(i.timestamp) >= monthAgo).length,
      lastWeek:  all.filter(i => new Date(i.timestamp) >= weekAgo).length,
    };
  }

  get selectedZoneTeacherName(): string {
    if (!this.selectedZoneId) return '';
    return (
      this.activeShifts.find(s => s.zoneId === this.selectedZoneId)?.teacherName ??
      this.todayShifts.find(s => s.zoneId === this.selectedZoneId)?.teacherName ??
      'Sin docente asignado'
    );
  }

  get selectedZoneTeacherLabel(): string {
    if (!this.selectedZoneId) return '';
    return this.activeShifts.some(s => s.zoneId === this.selectedZoneId)
      ? 'Docente actualmente asignado'
      : 'Docente asignado para la jornada';
  }

  onBack(): void { void this.router.navigateByUrl('/coordinator'); }

  toggleAutoRefresh(): void {
    this.autoRefresh = !this.autoRefresh;
  }

  retryLoad(): void { this.loadData(); }

  viewShiftDetails(shift: CoordinatorShift): void { this.selectedShift = shift; }
  viewZoneDetails(zoneId: string): void            { this.selectedZoneId = zoneId; }
  closeShiftDetails(): void                        { this.selectedShift = null; }
  closeZoneDetails(): void                         { this.selectedZoneId = null; }

  openReassignDialog(shift: CoordinatorShift): void {
    this.reassignShift = shift;
    this.reassignError = null;
  }

  closeReassignDialog(): void {
    this.reassignShift = null;
    this.reassignError = null;
    this.reassigning   = false;
  }

  confirmReassign(shift: CoordinatorShift, teacherId: string): void {
    const teacher = this.teachers.find(t => t.id === teacherId);
    if (!teacher) return;

    this.reassigning   = true;
    this.reassignError = null;

    const payload = {
      motivo:          'Reasignación solicitada por coordinador',
      fecha_propuesta: new Date().toISOString().slice(0, 19),
      estado:          'PENDIENTE',
      turno:            { id_turno:   Number(shift.id) },
      docenteOriginal:  { id_usuario: Number(shift.teacherId) },
      docentePropuesto: { id_usuario: Number(teacherId) },
    };

    this.reasignSvc.create(payload).subscribe({
      next: () => {
        const idx = this.shifts.findIndex(s => s.id === shift.id);
        if (idx !== -1) {
          this.shifts = [
            ...this.shifts.slice(0, idx),
            { ...this.shifts[idx], teacherId, teacherName: teacher.name },
            ...this.shifts.slice(idx + 1),
          ];
        }
        this.reassigning = false;
        this.closeReassignDialog();
      },
      error: (err) => {
        console.error('Error al reasignar:', err);
        this.reassignError = 'No se pudo completar la reasignación. Intenta de nuevo.';
        this.reassigning   = false;
      },
    });
  }
}
