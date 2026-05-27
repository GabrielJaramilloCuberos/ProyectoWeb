import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, combineLatest, map, of, catchError, startWith, switchMap } from 'rxjs';

import { AdminService, Zone, Teacher, Shift } from '../../services/admin.service';
import { UsuarioService } from '../../../../core/services/usuario.service';
import { Auth } from '../../../../core/services/auth';
import { StatCard } from '../../../../shared/ui/stat-card/stat-card';
import { ModuleCard } from '../../../../shared/ui/module-card/module-card';
import { Modal } from '../../../../shared/ui/modal/modal';
import { PageHeader } from '../../../../shared/ui/page-header/page-header';
import { MOCK_TEACHERS, MOCK_SHIFTS, MOCK_ZONES } from '../../admin.mock-data';

interface StatDetail {
  id: 'zones' | 'teachers' | 'shifts' | 'compliance';
  label: string;
  value: string | number;
  icon: 'map' | 'users' | 'calendar' | 'trending';
  color: 'blue' | 'purple' | 'green' | 'orange';
  details: string[];
}

interface HomeVM {
  stats: StatDetail[];
  completedShifts: number;
  activeZones: number;
  totalZones: number;
  teachers: number;
}

const USUARIO_FALLBACK = { nombre: 'Administrador' };

@Component({
  selector: 'admin-home',
  standalone: true,
  imports: [CommonModule, StatCard, ModuleCard, Modal, PageHeader],
  templateUrl: './home.html'
})
export class Home {

  private adminService   = inject(AdminService);
  private usuarioService = inject(UsuarioService);
  private auth           = inject(Auth);
  private router         = inject(Router);

  usuario$: Observable<any> = this.usuarioService.getUsuarioActual().pipe(
    catchError(() => of(USUARIO_FALLBACK))
  );

  viewModel$: Observable<HomeVM>;

  selectedStat: StatDetail | null = null;

  constructor() {
    const zones$    = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getZones().pipe(
        map(zones => zones.length > 0 ? zones : MOCK_ZONES),
        catchError(() => of(MOCK_ZONES))
      ))
    );
    const teachers$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getTeachers().pipe(
        map(teachers => teachers.length > 0 ? teachers : MOCK_TEACHERS),
        catchError(() => of(MOCK_TEACHERS))
      ))
    );
    const shifts$   = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getShifts().pipe(
        map(shifts => shifts.length > 0 ? shifts : MOCK_SHIFTS),
        catchError(() => of(MOCK_SHIFTS))
      ))
    );

    this.viewModel$ = combineLatest([zones$, teachers$, shifts$]).pipe(
      map(([zones, teachers, shifts]) => this.buildViewModel(zones, teachers, shifts))
    );
  }

  private buildViewModel(zones: Zone[], teachers: Teacher[], shifts: Shift[]): HomeVM {
    const today          = new Date().toISOString().split('T')[0];
    const todayShifts    = shifts.filter(s => s.date === today);
    const activeZones    = zones.filter(z => z.isActive);
    const activeTeachers = teachers.filter(t => t.isActive);
    const completedShifts = todayShifts.filter(s => s.status === 'completed');
    const complianceRate = todayShifts.length > 0
      ? Math.round((completedShifts.length / todayShifts.length) * 100)
      : 0;

    const stats: StatDetail[] = [
      {
        id: 'zones', label: 'Zonas Activas', value: activeZones.length,
        icon: 'map', color: 'blue',
        details: activeZones.map(z => z.name)
      },
      {
        id: 'teachers', label: 'Docentes', value: activeTeachers.length,
        icon: 'users', color: 'purple',
        details: activeTeachers.map(t => `${t.name} - ${t.department}`)
      },
      {
        id: 'shifts', label: 'Turnos Hoy', value: todayShifts.length,
        icon: 'calendar', color: 'green',
        details: todayShifts.map(s => `${s.zoneName} - ${s.teacherName} (${s.startTime})`)
      },
      {
        id: 'compliance', label: 'Cumplimiento', value: `${complianceRate}%`,
        icon: 'trending', color: 'orange',
        details: [`${completedShifts.length} de ${todayShifts.length} turnos completados`]
      }
    ];

    return {
      stats,
      completedShifts: completedShifts.length,
      activeZones: activeZones.length,
      totalZones: zones.length,
      teachers: activeTeachers.length
    };
  }

  openStat(stat: StatDetail): void {
    this.selectedStat = stat;
  }

  closeStat(): void {
    this.selectedStat = null;
  }

  cerrarSesion(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
