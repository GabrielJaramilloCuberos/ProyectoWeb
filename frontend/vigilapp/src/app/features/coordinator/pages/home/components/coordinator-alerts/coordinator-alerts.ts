import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorShift } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-alerts',
  imports: [CommonModule],
  templateUrl: './coordinator-alerts.html',
  styleUrl: './coordinator-alerts.css',
})
export class CoordinatorAlerts {
  @Input() missedShifts: CoordinatorShift[] = [];

  @Output() viewDetails = new EventEmitter<CoordinatorShift>();
  @Output() reassign = new EventEmitter<CoordinatorShift>();
}
