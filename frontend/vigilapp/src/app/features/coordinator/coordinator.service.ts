import { Injectable } from '@angular/core';
import { forkJoin, Observable, map } from 'rxjs';

import { TurnoService, TurnoBackend } from '../../core/services/turno.service';
import { IncidenteService, Incidente } from '../../core/services/incidente.service';
import { ZonaService, Zona } from '../../core/services/zona.service';
import { CheckpointService, Checkpoint } from '../../core/services/checkpoint.service';
import {
  CoordinatorIncident,
  CoordinatorShift,
  CoordinatorTeacher,
  CoordinatorZone,
  ShiftStatus,
  IncidentSeverity,
} from './pages/home/coordinator-home.models';

export interface CoordinatorDashboardData {
  shifts:    CoordinatorShift[];
  incidents: CoordinatorIncident[];
  zones:     CoordinatorZone[];
  teachers:  CoordinatorTeacher[];
  today:     string;
}

const ESTADO_MAP: Record<string, ShiftStatus> = {
  ACTIVO:     'active',
  COMPLETADO: 'completed',
  AUSENTE:    'missed',
  PENDIENTE:  'pending',
  DISPONIBLE: 'available',
  ASIGNADO:   'assigned',
};

@Injectable({ providedIn: 'root' })
export class CoordinatorService {

  constructor(
    private turnoService:     TurnoService,
    private incidenteService: IncidenteService,
    private zonaService:      ZonaService,
    private checkpointService: CheckpointService,
  ) {}

  loadDashboard(): Observable<CoordinatorDashboardData> {
    console.log('🔵 loadDashboard llamado');
    return forkJoin({
      turnos:     this.turnoService.getTurnos(),
      incidentes: this.incidenteService.getIncidentes(),
      zonas:      this.zonaService.getZonas(),
      checkpoints: this.checkpointService.getCheckpoints(),
    }).pipe(
      map(({ turnos, incidentes, zonas, checkpoints }) => {
        console.log('🟢 forkJoin completó', turnos, incidentes, zonas, checkpoints);
        const today = new Date().toISOString().slice(0, 10);

        const shifts    = turnos.map(t => this.mapTurno(t));
        const incidents = incidentes.map(i => this.mapIncidente(i, turnos));
        const zones     = zonas.map(z => this.mapZona(z, incidentes, checkpoints));

        const teacherMap = new Map<number, CoordinatorTeacher>();
        turnos.forEach(t => {
          if (!teacherMap.has(t.docente.id_usuario)) {
            teacherMap.set(t.docente.id_usuario, {
              id:         String(t.docente.id_usuario),
              name:       t.docente.nombre,
              department: t.zona.nombre,
              isActive:   t.docente.estado,
            });
          }
        });
        const teachers = Array.from(teacherMap.values());

        return { shifts, incidents, zones, teachers, today };
      })
    );
  }

  private mapTurno(t: TurnoBackend): CoordinatorShift {
    return {
      id:          String(t.id_turno),
      zoneId:      String(t.zona.id_zona),
      zoneName:    t.zona.nombre,
      teacherId:   String(t.docente.id_usuario),
      teacherName: t.docente.nombre,
      startTime:   t.hora_inicio.slice(0, 5),
      endTime:     t.hora_fin.slice(0, 5),
      status:      ESTADO_MAP[t.estado.toUpperCase()] ?? 'pending',
      date:        t.fecha,
    };
  }

  private mapIncidente(i: Incidente, turnos: TurnoBackend[]): CoordinatorIncident {
    const turno = turnos.find(t => t.id_turno === i.turno?.id_turno);
    return {
      id:          String(i.id_incidente),
      zoneId:      String(i.zona?.id_zona ?? turno?.zona.id_zona ?? 0),
      zoneName:    turno?.zona.nombre ?? i.zona?.nombre ?? 'Desconocida',
      teacherName: turno?.docente.nombre ?? 'Desconocido',
      timestamp:   i.fecha_hora,
      severity:    this.mapSeveridad(i.severidad?.codigo),
      description: i.descripcion,
    };
  }

  private mapZona(z: Zona, incidentes: Incidente[], checkpoints: Checkpoint[]): CoordinatorZone {
    const count = incidentes.filter(i => i.zona?.id_zona === z.id_zona).length;
    const zonaCheckpoints = checkpoints
      .filter(cp => cp.zona.id_zona === z.id_zona && cp.activo)
      .map(cp => cp.nombre);

    return {
      id:            String(z.id_zona),
      name:          z.nombre,
      description:   z.descripcion,
      capacity:      zonaCheckpoints.length,
      checkpoints:   zonaCheckpoints,
      intensidad:    Math.min(10, Math.max(1, count * 2)),
      incidentCount: count,
    };
  }

  private mapSeveridad(codigo: string | undefined): IncidentSeverity {
    const upper = (codigo ?? '').toUpperCase();
    if (upper === 'S1' || upper === 'S2' || upper === 'S3') return upper as IncidentSeverity;
    return 'S1';
  }
}
