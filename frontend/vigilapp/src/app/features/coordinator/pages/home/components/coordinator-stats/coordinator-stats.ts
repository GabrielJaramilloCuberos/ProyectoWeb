import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-coordinator-stats',
  imports: [CommonModule],
  templateUrl: './coordinator-stats.html',
  styleUrl: './coordinator-stats.css',
})
export class CoordinatorStats {
  @Input() activeShifts = 0;
  @Input() missedShifts = 0;
  @Input() patrolCount = 0;
  @Input() incidents = 0;
}
