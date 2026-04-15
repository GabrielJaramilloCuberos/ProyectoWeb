import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-coordinator-header',
  imports: [CommonModule],
  templateUrl: './coordinator-header.html',
  styleUrl: './coordinator-header.css',
})
export class CoordinatorHeader {
  @Input() autoRefresh = true;

  @Output() back = new EventEmitter<void>();
  @Output() toggleAutoRefresh = new EventEmitter<void>();
}
