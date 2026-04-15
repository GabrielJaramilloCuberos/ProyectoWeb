import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorShift, CoordinatorZone } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-zone-map',
  imports: [CommonModule],
  templateUrl: './coordinator-zone-map.html',
  styleUrl: './coordinator-zone-map.css',
})
export class CoordinatorZoneMap {
  @Input() zones: CoordinatorZone[] = [];
  @Input() activeShifts: CoordinatorShift[] = [];

  @Output() zoneSelected = new EventEmitter<CoordinatorZone>();

  getZoneStatus(zoneName: string): { status: string; label: string } {
    const hasActiveShift = this.activeShifts.some(shift => shift.zoneName === zoneName);

    if (hasActiveShift) {
      return { status: 'activo', label: 'Activo' };
    }

    return { status: 'inactivo', label: 'Inactivo' };
  }

  getZoneStatusClasses(status: string): { icon: string; iconText: string; badge: string } {
    switch (status) {
      case 'activo':
        return {
          icon: 'bg-emerald-100',
          iconText: 'text-emerald-600',
          badge: 'bg-emerald-100 text-emerald-700'
        };
      default:
        return {
          icon: 'bg-slate-100',
          iconText: 'text-slate-600',
          badge: 'bg-slate-100 text-slate-700'
        };
    }
  }

  getZoneShift(zoneName: string): CoordinatorShift | undefined {
    return this.activeShifts.find(shift => shift.zoneName === zoneName);
  }
}
