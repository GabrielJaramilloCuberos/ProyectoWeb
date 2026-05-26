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
  @Input() loading = false;
  @Input() errorMessage: string | null = null;

  @Output() close   = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<{ shift: CoordinatorShift; teacherId: string }>();

  protected selectedTeacherId = '';

  get filteredTeachers(): CoordinatorTeacher[] {
    return this.availableTeachers.filter(t => t.id !== this.shift?.teacherId);
  }

  onConfirm(): void {
    if (!this.shift || !this.selectedTeacherId || this.loading) return;
    this.confirm.emit({ shift: this.shift, teacherId: this.selectedTeacherId });
  }

  onClose(): void {
    if (this.loading) return;
    this.selectedTeacherId = '';
    this.close.emit();
  }
}
