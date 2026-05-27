import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Observable, Subject, of, map, catchError, takeUntil, shareReplay } from 'rxjs';

import { UsuarioService } from '../../../../core/services/usuario.service';
import { TurnoService } from '../../../../core/services/turno.service';
import { MetricasService } from '../../../../core/services/metricas.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';

const TODAY = new Date().toISOString().split('T')[0];
const USUARIO_FALLBACK    = { nombre: 'Usuario' };
const METRICAS_FALLBACK   = { puntos_totales: 1240, ranking: 3, puntualidad_porcentaje: 96, recorridos_promedio: 8 };
const NOTIFICACIONES_FALLBACK = [
  { id: 1, leida: false, mensaje: 'Turno confirmado para Parque Preescolar.' },
  { id: 2, leida: false, mensaje: 'Nuevo recordatorio para el recorrido de 10:00 AM.' },
  { id: 3, leida: true, mensaje: 'Ranking del mes actualizado.' },
];

const MOCK_TURNOS = [
  { id: 101, estado: 'DISPONIBLE', fecha: TODAY, hora_inicio: '09:00', hora_fin: '11:00', docenteAnterior: 'Sofía Rojas', zona: { nombre: 'Parque Preescolar' } },
  { id: 102, estado: 'ASIGNADO', fecha: TODAY, hora_inicio: '11:00', hora_fin: '13:00', zona: { nombre: 'Coliseo' } },
  { id: 103, estado: 'COMPLETADO', fecha: TODAY, hora_inicio: '07:00', hora_fin: '09:00', zona: { nombre: 'Cancha de Baloncesto' } },
  { id: 104, estado: 'COMPLETADO', fecha: TODAY, hora_inicio: '06:00', hora_fin: '08:00', zona: { nombre: 'Cafetería Bachillerato' } },
  { id: 105, estado: 'ASIGNADO', fecha: TODAY, hora_inicio: '14:00', hora_fin: '16:00', zona: { nombre: 'Parque Bachillerato Central' } },
];

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html'
})
export class Home implements OnDestroy {

  usuario$!:              Observable<any>;
  unreadCount$!:          Observable<number>;
  turnosDisponibles$!:    Observable<any[]>;
  proximosTurnos$!:       Observable<any[]>;
  turnosCompletadosHoy$!: Observable<any[]>;
  metricas$!:             Observable<any>;

  mostrarNotificaciones = false;
  mostrarRanking = false;

  private destroy$ = new Subject<void>();

  constructor(
    private usuarioService: UsuarioService,
    private turnoService: TurnoService,
    private metricasService: MetricasService,
    private notificacionService: NotificacionService,
    private router: Router
  ) {
    /* ── Usuario ── */
    this.usuario$ = this.usuarioService.getUsuarioActual().pipe(
      catchError(() => of(USUARIO_FALLBACK))
    );

    /* ── Notificaciones no leídas ── */
    this.unreadCount$ = this.notificacionService.getNotificaciones().pipe(
      map((n: any[]) => (n.length > 0 ? n : NOTIFICACIONES_FALLBACK).filter(notif => !notif.leida).length),
      catchError(() => of(NOTIFICACIONES_FALLBACK.filter(notif => !notif.leida).length))
    );

    /* ── Turnos (compartido para evitar múltiples peticiones) ── */
    const turnos$ = this.turnoService.getTurnos().pipe(
      map((turnos: any[]) => turnos.length > 0 ? turnos : MOCK_TURNOS),
      catchError(() => of(MOCK_TURNOS)),
      shareReplay(1)
    );

    /* DISPONIBLES */
    this.turnosDisponibles$ = turnos$.pipe(
      map((turnos: any[]) => turnos.filter(t => t.estado === 'DISPONIBLE'))
    );

    /* PRÓXIMOS */
    this.proximosTurnos$ = turnos$.pipe(
      map((turnos: any[]) => turnos.filter(t => t.estado === 'ASIGNADO'))
    );

    /* COMPLETADOS HOY */
    this.turnosCompletadosHoy$ = turnos$.pipe(
      map((turnos: any[]) => {
        const hoy = new Date().toISOString().split('T')[0];
        return turnos.filter(t =>
          t.estado === 'COMPLETADO' && t.fecha?.startsWith(hoy)
        );
      })
    );

    /* ── Métricas — siempre emite algo ── */
    this.metricas$ = this.metricasService.getMetricasDocente().pipe(
      catchError(() => of(METRICAS_FALLBACK)),
      map(m => (m && Object.keys(m).length > 0 ? m : METRICAS_FALLBACK))
    );
  }

  tomarTurno(turno: any): void {
    this.turnoService.tomarTurno(turno.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({ error: err => console.error('Error al tomar turno:', err) });
  }

  noAsistir(turno: any): void {
    this.turnoService.reasignarTurno(turno.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({ error: err => console.error('Error al reasignar turno:', err) });
  }

  cerrarSesion(): void {
    this.usuarioService.logout?.();
    this.router.navigate(['/']);
  }

  calcularEstrellas(metricas: any): number {
    if (!metricas) return 0;
    const puntualidad = metricas.puntualidad_porcentaje ?? 0;
    const recorridos  = metricas.recorridos_promedio   ?? 0;
    const score       = (puntualidad + recorridos) / 2;
    return Math.min(5, Math.max(0, Math.round(score / 20)));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}