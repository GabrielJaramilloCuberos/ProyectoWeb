import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';

import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { ZonaService, Zona } from '../../../../core/services/zona.service';
import { TipoIncidenteService, TipoIncidente } from '../../../../core/services/tipo-incidente.service';
import { SeveridadService, Severidad } from '../../../../core/services/severidad.service';
import { DepartamentoService, Departamento } from '../../../../core/services/departamento.service';
import { IncidenteService } from '../../../../core/services/incidente.service';
import { TurnoService } from '../../../../core/services/turno.service';

/* ── Subtítulos descriptivos por nombre de tipo
   (el backend solo guarda 'nombre', estos textos enriquecen la UI) ── */
const SUBTITULO_TIPO: Record<string, string> = {
  'Seguridad Física':   'Caída, golpe, accidente',
  'Convivencia':        'Pelea, agresión, conflicto',
  'Uso del Espacio':    'Mal uso de instalaciones',
  'Observación Social': 'Aislamiento, conducta',
};

/* ── Nombre amigable por código de severidad ── */
const NOMBRE_SEVERIDAD: Record<string, string> = {
  'S1': 'Leve',
  'S2': 'Seguimiento',
  'S3': 'Urgente',
};

@Component({
  selector: 'app-reporte',
  imports: [CommonModule, FormsModule, GlassCard],
  templateUrl: './reporte.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Reporte {
  private router              = inject(Router);
  private zonaService         = inject(ZonaService);
  private tipoService         = inject(TipoIncidenteService);
  private severidadService    = inject(SeveridadService);
  private departamentoService = inject(DepartamentoService);
  private incidenteService    = inject(IncidenteService);
  private turnoService        = inject(TurnoService);

  /* ── Catálogos cargados desde el backend ── */
  zonas       = toSignal(
    this.zonaService.getZonas().pipe(catchError(() => of([] as Zona[]))),
    { initialValue: [] as Zona[] }
  );
  tipos       = toSignal(
    this.tipoService.getTipos().pipe(catchError(() => of([] as TipoIncidente[]))),
    { initialValue: [] as TipoIncidente[] }
  );
  severidades = toSignal(
    this.severidadService.getSeveridades().pipe(catchError(() => of([] as Severidad[]))),
    { initialValue: [] as Severidad[] }
  );
  departamentos = toSignal(
    this.departamentoService.getDepartamentos().pipe(catchError(() => of([] as Departamento[]))),
    { initialValue: [] as Departamento[] }
  );

  /* ── Turno activo del docente (primer ASIGNADO o EN_CURSO) ── */
  turnoActivoId = toSignal(
    this.turnoService.getTurnos().pipe(
      map((turnos: any[]) =>
        turnos.find(t => t.estado === 'ASIGNADO' || t.estado === 'EN_CURSO')?.id_turno ?? null
      ),
      catchError(() => of(null))
    ),
    { initialValue: null as number | null }
  );

  /* ── Estado del formulario ── */
  zonaSeleccionada           = signal<number | null>(null);
  tipoSeleccionado           = signal<number | null>(null);
  severidadSeleccionada      = signal<number | null>(null);
  descripcion                = signal<string>('');
  departamentosSeleccionados = signal<Set<number>>(new Set());

  enviando     = signal<boolean>(false);
  mensajeError = signal<string | null>(null);
  mensajeExito = signal<string | null>(null);

  /* ── Validación reactiva ── */
  formularioCompleto = computed(() =>
    this.zonaSeleccionada()      !== null &&
    this.tipoSeleccionado()      !== null &&
    this.severidadSeleccionada() !== null &&
    this.descripcion().trim().length > 0 &&
    this.turnoActivoId()         !== null
  );

  /* ── Helpers UI ── */
  subtituloTipo(nombre: string): string {
    return SUBTITULO_TIPO[nombre] ?? '';
  }

  nombreSeveridad(codigo: string): string {
    return NOMBRE_SEVERIDAD[codigo] ?? codigo;
  }

  /* ── Selecciones ── */
  seleccionarZona(id: number)      { this.zonaSeleccionada.set(id); }
  seleccionarTipo(id: number)      { this.tipoSeleccionado.set(id); }
  seleccionarSeveridad(id: number) { this.severidadSeleccionada.set(id); }

  toggleDepartamento(id: number) {
    const actual = new Set(this.departamentosSeleccionados());
    if (actual.has(id)) actual.delete(id);
    else actual.add(id);
    this.departamentosSeleccionados.set(actual);
  }

  onDescripcionChange(valor: string) {
    this.descripcion.set(valor);
  }

  cancelar() {
    this.router.navigate(['/profesor/home']);
  }

  registrar() {
    if (!this.formularioCompleto() || this.enviando()) return;

    const idTurno = this.turnoActivoId();
    if (idTurno === null) {
      this.mensajeError.set('No tienes un turno activo. Solo puedes reportar durante un turno.');
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set(null);
    this.mensajeExito.set(null);

    this.incidenteService.crearIncidente({
      fecha_hora:    new Date().toISOString(),
      descripcion:   this.descripcion().trim(),
      turno:         { id_turno:     idTurno },
      zona:          { id_zona:      this.zonaSeleccionada()! },
      tipoIncidente: { id_tipo:      this.tipoSeleccionado()! },
      severidad:     { id_severidad: this.severidadSeleccionada()! },
    }).subscribe({
      next: () => {
        this.enviando.set(false);
        this.mensajeExito.set('Incidente registrado correctamente');
        setTimeout(() => this.router.navigate(['/profesor/home']), 1200);
      },
      error: (err) => {
        this.enviando.set(false);
        this.mensajeError.set(
          err?.error?.message ?? 'Error al registrar el incidente. Intenta de nuevo.'
        );
        console.error('Error al crear incidente:', err);
      }
    });
  }
}
