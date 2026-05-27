import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Observable, of, catchError, map, startWith, switchMap } from 'rxjs';

import { AdminService, Zone } from '../../services/admin.service';
import { PageHeader } from '../../../../shared/ui/page-header/page-header';
import { Modal } from '../../../../shared/ui/modal/modal';
import { GlassCard } from '../../../../shared/ui/glass-card/glass-card';
import { MOCK_ZONES } from '../../admin.mock-data';

@Component({
  selector: 'admin-zones',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeader, Modal, GlassCard],
  templateUrl: './zones.html'
})
export class Zones {
  private adminService = inject(AdminService);

  zones$: Observable<Zone[]> = this.adminService.refresh$.pipe(
    startWith(undefined as void),
    switchMap(() => this.adminService.getZones().pipe(
      map((zones: Zone[]) => zones.length > 0 ? zones : MOCK_ZONES),
      catchError(() => of(MOCK_ZONES))
    ))
  );

  isAddOpen = false;
  editingZone: Zone | null = null;

  form = { name: '', description: '', checkpoints: '' };

  openAdd(): void {
    this.form = { name: '', description: '', checkpoints: '' };
    this.isAddOpen = true;
  }

  openEdit(zone: Zone): void {
    this.editingZone = zone;
    this.form = {
      name: zone.name,
      description: zone.description,
      checkpoints: zone.checkpoints.join(', ')
    };
  }

  closeAdd(): void  { this.isAddOpen = false; }
  closeEdit(): void { this.editingZone = null; }

  private parseCheckpoints(raw: string): string[] {
    return raw.split(',').map(c => c.trim()).filter(c => c.length > 0);
  }

  addZone(): void {
    if (!this.form.name.trim() || !this.form.description.trim()) return;
    this.adminService.addZone({
      name: this.form.name.trim(),
      description: this.form.description.trim(),
      checkpoints: this.parseCheckpoints(this.form.checkpoints),
      isActive: true
    }).subscribe({
      next: () => {
        this.adminService.notifyChange();
        this.closeAdd();
      },
      error: err => console.error('Error al crear zona:', err)
    });
  }

  updateZone(): void {
    if (!this.editingZone) return;
    this.adminService.updateZone(this.editingZone.id, {
      name: this.form.name.trim(),
      description: this.form.description.trim(),
      checkpoints: this.parseCheckpoints(this.form.checkpoints)
    }).subscribe({
      next: () => {
        this.adminService.notifyChange();
        this.closeEdit();
      },
      error: err => console.error('Error al actualizar zona:', err)
    });
  }

  deleteZone(zone: Zone): void {
    if (!confirm(`¿Eliminar "${zone.name}"?`)) return;
    this.adminService.deleteZone(zone.id).subscribe({
      next: () => this.adminService.notifyChange(),
      error: err => console.error('Error al eliminar zona:', err)
    });
  }

  generateQR(zone: Zone): void {
    alert(`QR generado para ${zone.name}`);
  }
}
