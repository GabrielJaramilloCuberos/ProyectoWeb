import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { catchError, combineLatest, map, of } from 'rxjs';

import { IncidenteService, Incidente } from '../../../core/services/incidente.service';
import { ZonaService, Zona } from '../../../core/services/zona.service';

/**
 * Tabla con el porcentaje de incidentes registrados por cada zona.
 *
 * Cumple con el alcance mínimo del enunciado: "Mapas de calor por sector,
 * implementados como tabla que muestre el porcentaje de incidentes por cada
 * zona o sitio".
 *
 * Datos consumidos desde el backend:
 *   - GET /api/incidentes  (todos los incidentes)
 *   - GET /api/zonas       (todas las zonas)
 *
 * Cálculo: para cada zona, cuenta cuántos incidentes la referencian (por
 * id_zona) y muestra el porcentaje sobre el total. Ordena de mayor a menor.
 *
 * Componente standalone y reutilizable — puede integrarse en cualquier
 * dashboard (coordinador, admin, etc.) con un solo tag.
 */
interface FilaTabla {
  id_zona:    number;
  nombre:     string;
  cantidad:   number;
  porcentaje: number; // 0-100
}

@Component({
  selector: 'app-incidentes-por-zona-tabla',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './incidentes-por-zona-tabla.html',
})
export class IncidentesPorZonaTabla {
  private incidenteService = inject(IncidenteService);
  private zonaService      = inject(ZonaService);

  /* ── Carga combinada de incidentes y zonas ── */
  private data$ = combineLatest([
    this.incidenteService.getIncidentes().pipe(catchError(() => of([] as Incidente[]))),
    this.zonaService.getZonas().pipe(catchError(() => of([] as Zona[]))),
  ]).pipe(
    map(([incidentes, zonas]) => ({ incidentes, zonas }))
  );

  private state = toSignal(this.data$, {
    initialValue: { incidentes: [] as Incidente[], zonas: [] as Zona[] }
  });

  /* ── Datos derivados con computed (recalcula cuando cambia state) ── */

  totalIncidentes = computed(() => this.state().incidentes.length);

  filas = computed<FilaTabla[]>(() => {
    const { incidentes, zonas } = this.state();
    const total = incidentes.length;

    return zonas
      .map(zona => {
        const cantidad = incidentes.filter(i => i.zona?.id_zona === zona.id_zona).length;
        const porcentaje = total === 0 ? 0 : (cantidad / total) * 100;
        return {
          id_zona:    zona.id_zona,
          nombre:     zona.nombre,
          cantidad,
          porcentaje,
        };
      })
      .sort((a, b) => b.porcentaje - a.porcentaje);
  });

  cargando = computed(() => this.state().zonas.length === 0);

  /**
   * Color de la barra de progreso según el % de incidentes.
   * Mismo esquema que el heatmap visual (verde → amarillo → naranja → rojo).
   */
  colorBarra(porcentaje: number): string {
    if (porcentaje === 0)    return 'bg-slate-200';
    if (porcentaje <= 15)    return 'bg-emerald-400';
    if (porcentaje <= 35)    return 'bg-amber-400';
    if (porcentaje <= 60)    return 'bg-orange-500';
    return 'bg-red-600';
  }

  etiquetaIntensidad(porcentaje: number): string {
    if (porcentaje === 0)    return 'Sin incidentes';
    if (porcentaje <= 15)    return 'Baja';
    if (porcentaje <= 35)    return 'Media';
    if (porcentaje <= 60)    return 'Alta';
    return 'Crítica';
  }
}
