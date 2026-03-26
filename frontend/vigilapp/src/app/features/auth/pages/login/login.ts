import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { Mail } from '../../../../shared/ui/input/mail/mail';
import { Password } from '../../../../shared/ui/input/password/password';

@Component({
  selector: 'login',
  imports: [FormsModule, CommonModule,GlassCard, Mail, Password],
  templateUrl: './login.html',
  styleUrl: './login.css'
})

export class LoginPage {
    protected readonly title = signal('vigilapp');
  
  email = '';
  password = '';
  rememberMe = false;
  showPassword = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
