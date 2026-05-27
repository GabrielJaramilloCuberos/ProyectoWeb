import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { of, catchError } from 'rxjs';

import { AdminService, SystemConfig, DEFAULT_CONFIG } from '../../services/admin.service';
import { PageHeader } from '../../../../shared/ui/page-header/page-header';

@Component({
  selector: 'admin-config',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeader],
  templateUrl: './config.html'
})
export class Config implements OnInit {
  private adminService = inject(AdminService);
  private platformId = inject(PLATFORM_ID);

  config: SystemConfig = { ...DEFAULT_CONFIG };
  hasChanges = false;

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.adminService.getConfig().pipe(
      catchError(() => of<SystemConfig>({ ...DEFAULT_CONFIG }))
    ).subscribe(c => {
      this.config = { ...c };
      this.hasChanges = false;
    });
  }

  onChange<K extends keyof SystemConfig>(key: K, value: SystemConfig[K]): void {
    this.config[key] = value;
    this.hasChanges = true;
  }

  onNumberChange(key: keyof SystemConfig, value: string | number): void {
    this.config[key] = Number(value) as never;
    this.hasChanges = true;
  }

  adjust(key: 'minPatrols' | 'pointsPerShift' | 'pointsPerPatrol' | 'pointsPerReport',
         delta: number, min: number, max: number, step: number): void {
    const next = Math.min(max, Math.max(min, (this.config[key] as number) + delta * step));
    this.config[key] = next as never;
    this.hasChanges = true;
  }

  toggleGamification(): void {
    this.config.gamificationEnabled = !this.config.gamificationEnabled;
    this.hasChanges = true;
  }

  save(): void {
    this.adminService.updateConfig(this.config).subscribe({
      next: c => {
        this.config = { ...c };
        this.hasChanges = false;
      },
      error: err => console.error('Error al guardar configuración:', err)
    });
  }

  reset(): void {
    this.adminService.resetConfig().pipe(
      catchError(() => of<SystemConfig>({ ...DEFAULT_CONFIG }))
    ).subscribe(c => {
      this.config = { ...c };
      this.hasChanges = false;
    });
  }
}