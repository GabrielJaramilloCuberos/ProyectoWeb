import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorShift } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-active-shifts',
  imports: [CommonModule],
  templateUrl: './coordinator-active-shifts.html',
  styleUrl: './coordinator-active-shifts.css',
})
export class CoordinatorActiveShifts {
  @Input() shifts: CoordinatorShift[] = [];

  @Output() shiftSelected = new EventEmitter<CoordinatorShift>();
}
