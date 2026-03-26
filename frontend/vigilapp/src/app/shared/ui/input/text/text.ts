import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-text',
  imports: [],
  templateUrl: './text.html',
  styleUrl: './text.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Text {}
