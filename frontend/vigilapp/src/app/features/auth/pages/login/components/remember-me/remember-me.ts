import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-remember-me',
  imports: [FormsModule, CommonModule],
  templateUrl: './remember-me.html',
  styleUrl: './remember-me.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RememberMe {
  @Input() checked: boolean = false;
  @Input() delay: string = '0.5s';
  @Input() label: string = 'Recordar contraseña';
  
  @Output() checkedChange = new EventEmitter<boolean>();

  onCheckChange(value: boolean) {
    this.checkedChange.emit(value);
  }
}