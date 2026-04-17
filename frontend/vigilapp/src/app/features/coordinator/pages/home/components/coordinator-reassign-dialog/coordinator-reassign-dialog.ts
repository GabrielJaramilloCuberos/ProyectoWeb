import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CoordinatorShift, CoordinatorTeacher } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-reassign-dialog',
  imports: [CommonModule],
  templateUrl: './coordinator-reassign-dialog.html',
  styleUrl: './coordinator-reassign-dialog.css',
})
export class CoordinatorReassignDialog {
  @Input() shift: CoordinatorShift | null = null;
  @Input() availableTeachers: CoordinatorTeacher[] = [];

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<{ shift: CoordinatorShift; teacherId: string }>();
}
