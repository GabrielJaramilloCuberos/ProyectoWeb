import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorIncident, CoordinatorShift, CoordinatorZone } from '../../coordinator-home.models';

type ZoneHeatLevel = 'low' | 'medium' | 'high' | 'critical';

type ZoneLayout = {
  left: string;
  top: string;
  size: string;
};

type ZoneMetrics = {
  activeShift: CoordinatorShift | null;
  missedShift: CoordinatorShift | null;
  incidents: CoordinatorIncident[];
  heatScore: number;
  heatLevel: ZoneHeatLevel;
  statusLabel: string;
  statusDetail: string;
  gradient: string;
};

const ZONE_LAYOUTS: Record<string, ZoneLayout> = {
  z1: { left: '18%', top: '25%', size: '5.5rem' },
  z2: { left: '71%', top: '24%', size: '6rem' },
  z3: { left: '47%', top: '56%', size: '6.5rem' },
  z4: { left: '75%', top: '77%', size: '5.4rem' },
};

@Component({
  selector: 'app-coordinator-zone-map',
  imports: [CommonModule],
  templateUrl: './coordinator-zone-map.html',
  styleUrl: './coordinator-zone-map.css',
})
export class CoordinatorZoneMap {
  @Input() zones: CoordinatorZone[] = [];
  @Input() activeShifts: CoordinatorShift[] = [];
  @Input() missedShifts: CoordinatorShift[] = [];
  @Input() incidents: CoordinatorIncident[] = [];

  @Output() zoneSelected = new EventEmitter<CoordinatorZone>();

  getZoneMetrics(zone: CoordinatorZone): ZoneMetrics {
    const activeShift = this.activeShifts.find(shift => shift.zoneId === zone.id) ?? null;
    const missedShift = this.missedShifts.find(shift => shift.zoneId === zone.id) ?? null;
    const incidents = this.incidents.filter(incident => incident.zoneId === zone.id);
    const heatScore = this.getHeatScore(activeShift, missedShift, incidents);
    const heatLevel = this.getHeatLevel(heatScore);

    return {
      activeShift,
      missedShift,
      incidents,
      heatScore,
      heatLevel,
      statusLabel: this.getHeatLabel(heatLevel),
      statusDetail: this.getHeatDetail(heatLevel),
      gradient: this.getHeatGradient(heatLevel),
    };
  }

  getZoneLayout(zone: CoordinatorZone): ZoneLayout {
    return ZONE_LAYOUTS[zone.id] ?? { left: '50%', top: '52%', size: '5.8rem' };
  }

  getZoneCircleStyle(zone: CoordinatorZone): Record<string, string> {
    const layout = this.getZoneLayout(zone);
    const metrics = this.getZoneMetrics(zone);
    const opacity = Math.min(0.22, 0.06 + metrics.heatScore / 800);

    return {
      left: layout.left,
      top: layout.top,
      width: layout.size,
      height: layout.size,
      transform: 'translate(-50%, -50%)',
      background: metrics.gradient,
      opacity: String(opacity),
    };
  }

  getZoneDotStyle(level: ZoneHeatLevel): Record<string, string> {
    switch (level) {
      case 'critical':
        return {
          background: 'radial-gradient(circle at 35% 35%, #fecaca 0%, #fb7185 30%, #e11d48 78%)',
          boxShadow: '0 16px 28px rgba(225, 29, 72, 0.16)',
        };
      case 'high':
        return {
          background: 'radial-gradient(circle at 35% 35%, #fed7aa 0%, #fb923c 30%, #ea580c 78%)',
          boxShadow: '0 16px 28px rgba(234, 88, 12, 0.14)',
        };
      case 'medium':
        return {
          background: 'radial-gradient(circle at 35% 35%, #fde68a 0%, #fbbf24 30%, #d97706 78%)',
          boxShadow: '0 16px 28px rgba(217, 119, 6, 0.12)',
        };
      default:
        return {
          background: 'radial-gradient(circle at 35% 35%, #d1fae5 0%, #34d399 30%, #059669 78%)',
          boxShadow: '0 16px 28px rgba(5, 150, 105, 0.12)',
        };
    }
  }

  getZoneLabelStyle(zone: CoordinatorZone): Record<string, string> {
    const layout = this.getZoneLayout(zone);

    return {
      left: layout.left,
      top: `calc(${layout.top} + 4.8rem)`,
      transform: 'translateX(-50%)',
    };
  }

  getZoneCircleClasses(level: ZoneHeatLevel): string {
    switch (level) {
      case 'critical':
        return 'border-rose-300/70 hover:border-rose-400';
      case 'high':
        return 'border-orange-300/70 hover:border-orange-400';
      case 'medium':
        return 'border-amber-300/70 hover:border-amber-400';
      default:
        return 'border-emerald-300/70 hover:border-emerald-400';
    }
  }

  getHeatChipClasses(level: ZoneHeatLevel): string {
    switch (level) {
      case 'critical':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      case 'high':
        return 'bg-orange-50 text-orange-700 border-orange-200';
      case 'medium':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      default:
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
    }
  }

  getHeatRingClasses(level: ZoneHeatLevel): string {
    switch (level) {
      case 'critical':
        return 'shadow-[0_0_0_10px_rgba(251,113,133,0.10)]';
      case 'high':
        return 'shadow-[0_0_0_10px_rgba(251,146,60,0.10)]';
      case 'medium':
        return 'shadow-[0_0_0_10px_rgba(251,191,36,0.10)]';
      default:
        return 'shadow-[0_0_0_10px_rgba(52,211,153,0.10)]';
    }
  }

  private getHeatScore(
    activeShift: CoordinatorShift | null,
    missedShift: CoordinatorShift | null,
    incidents: CoordinatorIncident[]
  ): number {
    const severityWeights: Record<CoordinatorIncident['severity'], number> = {
      S1: 10,
      S2: 24,
      S3: 42,
    };

    const incidentScore = incidents.reduce((total, incident) => total + severityWeights[incident.severity], 0);
    const patrolReduction = Math.min(18, (activeShift?.patrolCount || 0) * 3);
    const presenceScore = activeShift ? 18 : 0;
    const missedPenalty = missedShift ? 32 : 0;

    return Math.max(0, Math.min(100, incidentScore + presenceScore + missedPenalty - patrolReduction));
  }

  private getHeatLevel(score: number): ZoneHeatLevel {
    if (score >= 75) {
      return 'critical';
    }

    if (score >= 50) {
      return 'high';
    }

    if (score >= 25) {
      return 'medium';
    }

    return 'low';
  }

  private getHeatLabel(level: ZoneHeatLevel): string {
    switch (level) {
      case 'critical':
        return 'Crítica';
      case 'high':
        return 'Alta';
      case 'medium':
        return 'Media';
      default:
        return 'Baja';
    }
  }

  private getHeatDetail(level: ZoneHeatLevel): string {
    switch (level) {
      case 'critical':
        return 'Concentración alta de alertas y cobertura débil.';
      case 'high':
        return 'Actividad elevada con señales de atención.';
      case 'medium':
        return 'Zona con actividad moderada y puntos de control.';
      default:
        return 'Cobertura estable y sin incidentes relevantes.';
    }
  }

  private getHeatGradient(level: ZoneHeatLevel): string {
    switch (level) {
      case 'critical':
        return 'radial-gradient(circle, rgba(251,113,133,0.28) 0%, rgba(251,113,133,0.18) 38%, rgba(251,113,133,0.08) 66%, rgba(251,113,133,0) 82%)';
      case 'high':
        return 'radial-gradient(circle, rgba(251,146,60,0.28) 0%, rgba(251,146,60,0.18) 38%, rgba(251,146,60,0.08) 66%, rgba(251,146,60,0) 82%)';
      case 'medium':
        return 'radial-gradient(circle, rgba(251,191,36,0.26) 0%, rgba(251,191,36,0.16) 38%, rgba(251,191,36,0.07) 66%, rgba(251,191,36,0) 82%)';
      default:
        return 'radial-gradient(circle, rgba(52,211,153,0.24) 0%, rgba(52,211,153,0.14) 38%, rgba(52,211,153,0.06) 66%, rgba(52,211,153,0) 82%)';
    }
  }
}
