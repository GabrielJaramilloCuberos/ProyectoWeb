import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorIncident, CoordinatorShift, CoordinatorZone } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-zone-details-dialog',
  imports: [CommonModule],
  templateUrl: './coordinator-zone-details-dialog.html',
  styleUrl: './coordinator-zone-details-dialog.css',
})
export class CoordinatorZoneDetailsDialog {
  @Input() zone: CoordinatorZone | null = null;
  @Input() shifts: CoordinatorShift[] = [];
  @Input() incidents: CoordinatorIncident[] = [];
  @Input() incidentSummary: { total: number; lastMonth: number; lastWeek: number } = {
    total: 0,
    lastMonth: 0,
    lastWeek: 0
  };
  @Input() currentTeacherName = '';
  @Input() currentTeacherLabel = '';

  @Output() close = new EventEmitter<void>();
}
