import { Component, Input, ChangeDetectionStrategy, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-mail',
  imports: [FormsModule, CommonModule],
  templateUrl: './mail.html',
  styleUrl: './mail.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Mail {
  @Input() label: string = 'Correo electrónico';
  @Input() placeholder: string = 'correo@colegio.edu';
  
  @Input() email: string = '';
  @Input() delay: string = '0.3s';


  @Output() emailChange = new EventEmitter<string>();
  onEmailChange(value: string) {
    this.emailChange.emit(value);
  }
}