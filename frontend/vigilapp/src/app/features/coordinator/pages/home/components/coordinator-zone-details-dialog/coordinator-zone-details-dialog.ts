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

  @Output() close = new EventEmitter<void>();
}
