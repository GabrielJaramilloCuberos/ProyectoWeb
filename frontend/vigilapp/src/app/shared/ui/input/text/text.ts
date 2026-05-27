import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-text',
  standalone: true,
  imports: [],
  templateUrl: './text.html',
  styleUrl: './text.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Text {}
