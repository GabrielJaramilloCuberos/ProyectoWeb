import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { Auth } from '../../../../core/services/auth';
import { ReasignacionService } from '../../../../core/services/reasignacion.service';
import { CoordinatorService } from '../../coordinator.service';
import {
  CoordinatorIncident,
  CoordinatorShift,
  CoordinatorTeacher,
} from '../home/coordinator-home.models';

@Component({
  selector: 'app-coordinator-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class CoordinatorDashboard implements OnInit, OnDestroy {
  private readonly router      = inject(Router);
  private readonly auth        = inject(Auth);
  private readonly coordSvc    = inject(CoordinatorService);
  private readonly reasignSvc  = inject(ReasignacionService);

  protected readonly loading      = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly userName  = signal<string>(this.auth.getUser()?.username ?? 'Coordinador');
  protected readonly shifts    = signal<CoordinatorShift[]>([]);
  protected readonly incidents = signal<CoordinatorIncident[]>([]);
  protected readonly teachers  = signal<CoordinatorTeacher[]>([]);
  protected readonly today     = signal<string>(new Date().toISOString().slice(0, 10));

  protected readonly showReassignDialog = signal(false);
  protected readonly showAssignDialog   = signal(false);
  protected readonly reassignShift      = signal<CoordinatorShift | null>(null);
  protected readonly assignShift        = signal<CoordinatorShift | null>(null);

  protected readonly todayShifts = computed(() =>
    this.shifts().filter(s => s.date === this.today())
  );

  protected readonly missedShifts = computed(() =>
    this.todayShifts().filter(s => s.status === 'missed')
  );

  protected readonly availableShift = computed<CoordinatorShift | null>(() => {
    const all = this.shifts();
    return all.find(s => s.status === 'available') ?? null;
  });

  protected readonly availableTeachers = computed(() =>
    this.teachers().filter(t => t.isActive)
  );

  private sub: Subscription | null = null;

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (!user) {
      void this.router.navigateByUrl('/login');
      return;
    }

    const role = (user.role ?? '').replace('ROLE_', '').toUpperCase();
    if (role !== 'COORDINADOR') {
      void this.router.navigateByUrl('/login');
      return;
    }

    this.userName.set(user.username);
    this.loadData();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private loadData(): void {
    this.sub?.unsubscribe();
    this.loading.set(true);
    this.errorMessage.set(null);

    this.sub = this.coordSvc.loadDashboard().subscribe({
      next: (data) => {
        this.today.set(data.today);
        this.shifts.set(data.shifts);
        this.incidents.set(data.incidents);
        this.teachers.set(data.teachers);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando dashboard del coordinador:', err);
        this.errorMessage.set('No se pudo conectar con el servidor.');
        this.loading.set(false);
      },
    });
  }

  retryLoad(): void {
    this.loadData();
  }

  onSignOut(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/login');
  }

  navigateToLive(): void {
    void this.router.navigateByUrl('/coordinator/live');
  }

  navigateToAnalytics(): void {
    void this.router.navigateByUrl('/coordinator/analytics');
  }

  openAssignDialog(shift: CoordinatorShift): void {
    this.assignShift.set(shift);
    this.showAssignDialog.set(true);
  }

  closeAssignDialog(): void {
    this.showAssignDialog.set(false);
    this.assignShift.set(null);
  }

  openReassignDialog(shift: CoordinatorShift): void {
    this.reassignShift.set(shift);
    this.showReassignDialog.set(true);
  }

  closeReassignDialog(): void {
    this.showReassignDialog.set(false);
    this.reassignShift.set(null);
  }

  confirmAssign(teacher: CoordinatorTeacher): void {
    const shift = this.assignShift();
    if (!shift) {
      this.closeAssignDialog();
      return;
    }

    const payload = {
      motivo:          'Asignación de turno disponible',
      fecha_propuesta: new Date().toISOString().slice(0, 19),
      estado:          'PENDIENTE',
      turno:            { id_turno:   Number(shift.id) },
      docenteOriginal:  { id_usuario: Number(shift.teacherId || teacher.id) },
      docentePropuesto: { id_usuario: Number(teacher.id) },
    };

    this.reasignSvc.create(payload).subscribe({
      next: () => {
        this.shifts.update(list =>
          list.map(s =>
            s.id === shift.id
              ? { ...s, teacherId: teacher.id, teacherName: teacher.name, status: 'assigned' }
              : s
          )
        );
        this.closeAssignDialog();
      },
      error: (err) => {
        console.error('Error asignando turno:', err);
        this.closeAssignDialog();
      },
    });
  }

  confirmReassign(teacher: CoordinatorTeacher): void {
    const shift = this.reassignShift();
    if (!shift) {
      this.closeReassignDialog();
      return;
    }

    const payload = {
      motivo:          'Reasignación solicitada por coordinador',
      fecha_propuesta: new Date().toISOString().slice(0, 19),
      estado:          'PENDIENTE',
      turno:            { id_turno:   Number(shift.id) },
      docenteOriginal:  { id_usuario: Number(shift.teacherId) },
      docentePropuesto: { id_usuario: Number(teacher.id) },
    };

    this.reasignSvc.create(payload).subscribe({
      next: () => {
        this.shifts.update(list =>
          list.map(s =>
            s.id === shift.id
              ? { ...s, teacherId: teacher.id, teacherName: teacher.name }
              : s
          )
        );
        this.closeReassignDialog();
      },
      error: (err) => {
        console.error('Error al reasignar:', err);
        this.closeReassignDialog();
      },
    });
  }
}
