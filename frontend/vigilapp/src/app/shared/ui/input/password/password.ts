import { Component, Input, signal, ChangeDetectionStrategy, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-password',
  imports: [FormsModule, CommonModule],
  templateUrl: './password.html',
  styleUrl: './password.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Password {
  @Input() label: string = 'Contrasena';
  @Input() placeholder: string = '********';
  @Input() password: string = '';
  @Input() delay: string = '0.4s';

  @Output() passwordChange = new EventEmitter<string>();

  showPassword = signal(false);

  togglePasswordVisibility() {
    this.showPassword.update(v => !v);
  }

  onPasswordInput(value: string) {
    this.passwordChange.emit(value);
  }
}