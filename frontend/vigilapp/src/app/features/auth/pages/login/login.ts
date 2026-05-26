import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { Mail } from '../../../../shared/ui/input/mail/mail';
import { Password } from '../../../../shared/ui/input/password/password';
import { RememberMe } from './components/remember-me/remember-me';
import { Auth } from '../../../../core/services/auth';
import { LoginResponse } from '../../../../core/models/user/login-response';

const ROUTES_BY_ROLE: Record<string, string> = {
  'coordinador': '/coordinator/dashboard',
  'profesor': '/profesor/home',
  'administrador': '/administrator/home',
  'admin': '/administrator/home',
};

@Component({
  selector: 'login',
  imports: [FormsModule, CommonModule, GlassCard, Mail, Password, RememberMe],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginPage {
  protected readonly title = signal('vigilapp');

  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  email = '';
  password = '';
  rememberMe = false;

  errorMessage = '';
  isLoading = false;

  onLogin() {
    this.errorMessage = '';

    if (!this.email.trim() || !this.password.trim()) {
      this.errorMessage = 'Correo y contraseña son obligatorios';
      return;
    }

    this.isLoading = true;

     this.auth.login(this.email.trim(), this.password).subscribe({
       next: (response: LoginResponse) => {
         this.isLoading = false;
         console.log('Login response:', response);

         this.auth.setUser(response, this.rememberMe);

         const route = ROUTES_BY_ROLE[response.role.toLowerCase()] || '/administrator/home';
         console.log('Navigating to:', route);
         this.router.navigateByUrl(route);
       },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Credenciales inválidas';
      }
    });

  }
}