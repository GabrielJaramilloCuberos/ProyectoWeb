import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorShift } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-shift-details-dialog',
  imports: [CommonModule],
  templateUrl: './coordinator-shift-details-dialog.html',
  styleUrl: './coordinator-shift-details-dialog.css',
})
export class CoordinatorShiftDetailsDialog {
  @Input() shift: CoordinatorShift | null = null;

  @Output() close = new EventEmitter<void>();
  @Output() reassign = new EventEmitter<CoordinatorShift>();
}
