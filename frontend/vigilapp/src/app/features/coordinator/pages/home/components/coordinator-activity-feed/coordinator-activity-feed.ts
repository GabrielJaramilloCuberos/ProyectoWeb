import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { CoordinatorIncident } from '../../coordinator-home.models';

@Component({
  selector: 'app-coordinator-activity-feed',
  imports: [CommonModule],
  templateUrl: './coordinator-activity-feed.html',
  styleUrl: './coordinator-activity-feed.css',
})
export class CoordinatorActivityFeed {
  @Input() incidents: CoordinatorIncident[] = [];
}
