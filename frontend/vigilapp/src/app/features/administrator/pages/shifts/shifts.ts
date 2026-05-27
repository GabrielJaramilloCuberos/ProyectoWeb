import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Observable, BehaviorSubject, combineLatest, map, of, catchError, startWith, switchMap
} from 'rxjs';

import { AdminService, Shift, Teacher, Zone, ShiftStatus } from '../../services/admin.service';
import { PageHeader } from '../../../../shared/ui/page-header/page-header';
import { Modal } from '../../../../shared/ui/modal/modal';
import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { StatCard } from '../../../../shared/ui/stat-card/stat-card';
import { MOCK_SHIFTS, MOCK_TEACHERS, MOCK_ZONES } from '../../admin.mock-data';

interface CalendarDay {
  date: string;
  day: number;
  inMonth: boolean;
  isToday: boolean;
  isSelected: boolean;
  total: number;
  covered: number;
  pending: number;
  missed: number;
}

interface ShiftsVM {
  today: Shift[];
  total: number;
  covered: number;
  pending: number;
  missed: number;
  teachers: Teacher[];
  zones: Zone[];
  monthLabel: string;
  monthDays: CalendarDay[];
  selectedDay: string | null;
  selectedDayShifts: Shift[];
}

@Component({
  selector: 'admin-shifts',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeader, Modal, GlassCard, StatCard],
  templateUrl: './shifts.html'
})
export class Shifts {
  private adminService = inject(AdminService);

  readonly today: string = new Date().toISOString().split('T')[0];
  readonly weekdayLabels = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

  vm$: Observable<ShiftsVM>;

  isAddOpen = false;
  isCalendarOpen = false;

  private calendarMonthSubject = new BehaviorSubject<Date>(this.firstOfMonth(new Date()));
  private selectedDaySubject   = new BehaviorSubject<string | null>(null);

  form = {
    teacherId: '',
    zoneId: '',
    date: this.today,
    startTime: '',
    endTime: ''
  };

  constructor() {
    const shifts$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getShifts().pipe(
        catchError(() => of(MOCK_SHIFTS))
      ))
    );
    const teachers$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getTeachers().pipe(
        catchError(() => of(MOCK_TEACHERS))
      ))
    );
    const zones$ = this.adminService.refresh$.pipe(
      startWith(undefined as void),
      switchMap(() => this.adminService.getZones().pipe(
        catchError(() => of(MOCK_ZONES))
      ))
    );

    this.vm$ = combineLatest([
      shifts$, teachers$, zones$,
      this.calendarMonthSubject, this.selectedDaySubject
    ]).pipe(
      map(([shifts, teachers, zones, month, selectedDay]) => {
        const today = shifts.filter(s => s.date === this.today);
        const monthDays = this.buildCalendarDays(month, shifts, selectedDay);
        const selectedDayShifts = selectedDay
          ? shifts.filter(s => s.date === selectedDay)
          : [];

        return {
          today,
          total:   today.length,
          covered: today.filter(s => s.status === 'completed' || s.status === 'active').length,
          pending: today.filter(s => s.status === 'pending').length,
          missed:  today.filter(s => s.status === 'missed').length,
          teachers,
          zones,
          monthLabel: this.formatMonthLabel(month),
          monthDays,
          selectedDay,
          selectedDayShifts
        };
      })
    );
  }

  statusLabel(status: ShiftStatus): string {
    return {
      completed: 'Completado',
      active: 'Activo',
      pending: 'Pendiente',
      missed: 'Sin cubrir'
    }[status];
  }

  statusClass(status: ShiftStatus): string {
    return {
      completed: 'bg-gray-100 text-gray-700',
      active:    'bg-green-100 text-green-700',
      pending:   'bg-yellow-100 text-yellow-700',
      missed:    'bg-red-100 text-red-700'
    }[status];
  }

  openAdd(): void {
    this.form = { teacherId: '', zoneId: '', date: this.today, startTime: '', endTime: '' };
    this.isAddOpen = true;
  }

  closeAdd(): void {
    this.isAddOpen = false;
  }

  addShift(teachers: Teacher[], zones: Zone[]): void {
    if (!this.form.teacherId || !this.form.zoneId || !this.form.startTime || !this.form.endTime) {
      return;
    }
    const teacher = teachers.find(t => t.id === this.form.teacherId);
    const zone    = zones.find(z => z.id === this.form.zoneId);
    if (!teacher || !zone) return;

    this.adminService.addShift({
      teacherId: teacher.id,
      teacherName: teacher.name,
      zoneId: zone.id,
      zoneName: zone.name,
      date: this.form.date,
      startTime: this.form.startTime,
      endTime: this.form.endTime,
      status: 'pending'
    }).subscribe({
      next: () => {
        this.adminService.notifyChange();
        this.closeAdd();
      },
      error: err => console.error('Error al crear turno:', err)
    });
  }

  deleteShift(shift: Shift): void {
    if (!confirm(`¿Eliminar el turno de ${shift.teacherName}?`)) return;
    this.adminService.deleteShift(shift.id).subscribe({
      next: () => this.adminService.notifyChange(),
      error: err => console.error('Error al eliminar turno:', err)
    });
  }

  bulkUpload(): void {
    alert('Carga masiva iniciada');
  }

  // -------- Calendar ----------

  viewCalendar(): void {
    this.calendarMonthSubject.next(this.firstOfMonth(new Date()));
    this.selectedDaySubject.next(this.today);
    this.isCalendarOpen = true;
  }

  closeCalendar(): void {
    this.isCalendarOpen = false;
  }

  prevMonth(): void {
    const m = this.calendarMonthSubject.value;
    this.calendarMonthSubject.next(new Date(m.getFullYear(), m.getMonth() - 1, 1));
  }

  nextMonth(): void {
    const m = this.calendarMonthSubject.value;
    this.calendarMonthSubject.next(new Date(m.getFullYear(), m.getMonth() + 1, 1));
  }

  goToToday(): void {
    this.calendarMonthSubject.next(this.firstOfMonth(new Date()));
    this.selectedDaySubject.next(this.today);
  }

  selectDay(day: CalendarDay): void {
    if (!day.inMonth) return;
    this.selectedDaySubject.next(day.date);
  }

  scheduleOnDate(date: string): void {
    this.form = { teacherId: '', zoneId: '', date, startTime: '', endTime: '' };
    this.isCalendarOpen = false;
    this.isAddOpen = true;
  }

  getHourSlots(): string[] {
    return Array.from({ length: 14 }, (_, i) => {
      const h = i + 6;
      return `${String(h).padStart(2, '0')}:00`;
    });
  }

  getShiftsForHour(shifts: Shift[], hour: string): Shift[] {
    return shifts.filter(s => s.startTime.substring(0, 2) === hour.substring(0, 2));
  }

  private firstOfMonth(d: Date): Date {
    return new Date(d.getFullYear(), d.getMonth(), 1);
  }

  private toISODate(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  private formatMonthLabel(d: Date): string {
    const months = ['Enero','Febrero','Marzo','Abril','Mayo','Junio',
                    'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'];
    return `${months[d.getMonth()]} ${d.getFullYear()}`;
  }

  private buildCalendarDays(month: Date, shifts: Shift[], selectedDay: string | null): CalendarDay[] {
    const year = month.getFullYear();
    const m    = month.getMonth();
    const firstDay = new Date(year, m, 1);
    const daysInMonth = new Date(year, m + 1, 0).getDate();

    const firstWeekday = (firstDay.getDay() + 6) % 7;

    const days: CalendarDay[] = [];

    for (let i = firstWeekday; i > 0; i--) {
      const d = new Date(year, m, 1 - i);
      days.push(this.makeDay(d, false, shifts, selectedDay));
    }

    for (let d = 1; d <= daysInMonth; d++) {
      days.push(this.makeDay(new Date(year, m, d), true, shifts, selectedDay));
    }

    while (days.length < 42) {
      const last = days[days.length - 1];
      const [ly, lm, ld] = last.date.split('-').map(Number);
      const next = new Date(ly, lm - 1, ld + 1);
      days.push(this.makeDay(next, false, shifts, selectedDay));
    }

    return days;
  }

  private makeDay(d: Date, inMonth: boolean, shifts: Shift[], selectedDay: string | null): CalendarDay {
    const dateStr = this.toISODate(d);
    const dayShifts = shifts.filter(s => s.date === dateStr);
    return {
      date: dateStr,
      day: d.getDate(),
      inMonth,
      isToday: dateStr === this.today,
      isSelected: dateStr === selectedDay,
      total:   dayShifts.length,
      covered: dayShifts.filter(s => s.status === 'completed' || s.status === 'active').length,
      pending: dayShifts.filter(s => s.status === 'pending').length,
      missed:  dayShifts.filter(s => s.status === 'missed').length
    };
  }
}