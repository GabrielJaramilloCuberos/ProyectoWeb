import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { CoordinatorActiveShifts } from './components/coordinator-active-shifts/coordinator-active-shifts';
import { CoordinatorActivityFeed } from './components/coordinator-activity-feed/coordinator-activity-feed';
import { CoordinatorAlerts } from './components/coordinator-alerts/coordinator-alerts';
import { CoordinatorHeader } from './components/coordinator-header/coordinator-header';
import { CoordinatorReassignDialog } from './components/coordinator-reassign-dialog/coordinator-reassign-dialog';
import { CoordinatorShiftDetailsDialog } from './components/coordinator-shift-details-dialog/coordinator-shift-details-dialog';
import { CoordinatorStats } from './components/coordinator-stats/coordinator-stats';
import { CoordinatorZoneDetailsDialog } from './components/coordinator-zone-details-dialog/coordinator-zone-details-dialog';
import { CoordinatorZoneMap } from './components/coordinator-zone-map/coordinator-zone-map';
import {
  COORDINATOR_DAY,
  MOCK_INCIDENTS,
  MOCK_SHIFTS,
  MOCK_TEACHERS,
  MOCK_ZONES,
  getCurrentUser,
} from './coordinator-home.data';
import {
  CoordinatorIncident,
  CoordinatorShift,
  CoordinatorTeacher,
  CoordinatorUser,
  CoordinatorZone,
} from './coordinator-home.models';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    CoordinatorHeader,
    CoordinatorStats,
    CoordinatorAlerts,
    CoordinatorZoneMap,
    CoordinatorActiveShifts,
    CoordinatorActivityFeed,
    CoordinatorShiftDetailsDialog,
    CoordinatorZoneDetailsDialog,
    CoordinatorReassignDialog
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  private readonly router = inject(Router);

  protected user: CoordinatorUser | null = null;
  protected autoRefresh = true;

  protected readonly today = COORDINATOR_DAY;
  protected readonly mockTeachers = MOCK_TEACHERS;
  protected readonly mockZones = MOCK_ZONES;
  protected readonly mockShifts = MOCK_SHIFTS;
  protected readonly mockIncidents = MOCK_INCIDENTS;

  protected selectedShift: CoordinatorShift | null = null;
  protected selectedZoneId: string | null = null;
  protected reassignShift: CoordinatorShift | null = null;

  private refreshHandle: number | null = null;

  ngOnInit(): void {
    this.user = getCurrentUser();

    if (!this.user || this.user.role !== 'coordinator') {
      void this.router.navigateByUrl('/login');
      return;
    }

    this.updateAutoRefresh();
  }

  ngOnDestroy(): void {
    this.stopAutoRefresh();
  }

  get todayShifts(): CoordinatorShift[] {
    return this.mockShifts.filter(shift => shift.date === this.today);
  }

  get activeShifts(): CoordinatorShift[] {
    return this.todayShifts.filter(shift => shift.status === 'active');
  }

  get missedShifts(): CoordinatorShift[] {
    return this.todayShifts.filter(shift => shift.status === 'missed');
  }

  get todayIncidents(): CoordinatorIncident[] {
    return this.mockIncidents.filter(incident => incident.timestamp.startsWith(this.today));
  }

  get patrolCount(): number {
    return this.activeShifts.reduce((total, shift) => total + (shift.patrolCount || 0), 0);
  }

  get selectedZoneData(): CoordinatorZone | null {
    return this.selectedZoneId
      ? this.mockZones.find(zone => zone.id === this.selectedZoneId) || null
      : null;
  }

  get selectedZoneShifts(): CoordinatorShift[] {
    return this.selectedZoneId
      ? this.todayShifts.filter(shift => shift.zoneId === this.selectedZoneId)
      : [];
  }

  get selectedZoneIncidents(): CoordinatorIncident[] {
    return this.selectedZoneId
      ? this.todayIncidents.filter(incident => incident.zoneId === this.selectedZoneId)
      : [];
  }

  get selectedZoneIncidentSummary(): { total: number; lastMonth: number; lastWeek: number } {
    if (!this.selectedZoneId) {
      return { total: 0, lastMonth: 0, lastWeek: 0 };
    }

    const incidents = this.mockIncidents.filter(incident => incident.zoneId === this.selectedZoneId);
    const referenceDate = new Date(`${this.today}T23:59:59`);
    const lastWeekThreshold = new Date(referenceDate);
    lastWeekThreshold.setDate(referenceDate.getDate() - 7);
    const lastMonthThreshold = new Date(referenceDate);
    lastMonthThreshold.setDate(referenceDate.getDate() - 30);

    return {
      total: incidents.length,
      lastMonth: incidents.filter(incident => new Date(incident.timestamp) >= lastMonthThreshold).length,
      lastWeek: incidents.filter(incident => new Date(incident.timestamp) >= lastWeekThreshold).length,
    };
  }

  get selectedZoneTeacherName(): string {
    if (!this.selectedZoneId) {
      return '';
    }

    const activeShift = this.activeShifts.find(shift => shift.zoneId === this.selectedZoneId);
    if (activeShift) {
      return activeShift.teacherName;
    }

    const todayShift = this.todayShifts.find(shift => shift.zoneId === this.selectedZoneId);
    return todayShift?.teacherName || 'Sin docente asignado';
  }

  get selectedZoneTeacherLabel(): string {
    if (!this.selectedZoneId) {
      return '';
    }

    return this.activeShifts.some(shift => shift.zoneId === this.selectedZoneId)
      ? 'Docente actualmente asignado'
      : 'Docente asignado para la jornada';
  }

  get availableTeachers(): CoordinatorTeacher[] {
    return this.mockTeachers.filter(teacher => teacher.isActive);
  }

  onBack(): void {
    void this.router.navigateByUrl('/coordinator');
  }

  toggleAutoRefresh(): void {
    this.autoRefresh = !this.autoRefresh;
    this.updateAutoRefresh();
  }

  viewShiftDetails(shift: CoordinatorShift): void {
    this.selectedShift = shift;
  }

  viewZoneDetails(zoneId: string): void {
    this.selectedZoneId = zoneId;
  }

  closeShiftDetails(): void {
    this.selectedShift = null;
  }

  closeZoneDetails(): void {
    this.selectedZoneId = null;
  }

  openReassignDialog(shift: CoordinatorShift): void {
    this.reassignShift = shift;
  }

  closeReassignDialog(): void {
    this.reassignShift = null;
  }

  confirmReassign(shift: CoordinatorShift, teacherId: string): void {
    const teacher = this.mockTeachers.find(item => item.id === teacherId);
    if (!teacher) {
      return;
    }

    this.closeReassignDialog();
    console.log(`Turno de ${shift.zoneName} reasignado a ${teacher.name}`);
  }

  private updateAutoRefresh(): void {
    this.stopAutoRefresh();

    if (!this.autoRefresh || typeof window === 'undefined') {
      return;
    }

    this.refreshHandle = window.setInterval(() => {
      console.log('Actualizando datos...');
    }, 5000);
  }

  private stopAutoRefresh(): void {
    if (this.refreshHandle !== null) {
      clearInterval(this.refreshHandle);
      this.refreshHandle = null;
    }
  }
}
