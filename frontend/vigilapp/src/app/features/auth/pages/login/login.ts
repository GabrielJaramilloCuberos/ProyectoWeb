import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { Mail } from '../../../../shared/ui/input/mail/mail';
import { Password } from '../../../../shared/ui/input/password/password';
import { RememberMe } from './components/remember-me/remember-me';
import { Auth } from '../../../../core/services/auth';
import { UsuarioLogin } from '../../../../core/models/user/user-login';

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
      next: (usuario: UsuarioLogin) => {
        this.isLoading = false;

        if (!usuario.estado) {
          this.errorMessage = 'Tu usuario está inactivo';
          return;
        }

        const storage = this.rememberMe ? localStorage : sessionStorage;
        storage.setItem('auth_user', JSON.stringify(usuario));

        this.router.navigateByUrl('/');
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Credenciales inválidas';
      }
    });

  }
}