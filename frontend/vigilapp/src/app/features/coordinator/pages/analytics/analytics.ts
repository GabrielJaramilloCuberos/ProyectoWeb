import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, forkJoin } from 'rxjs';

import { Auth } from '../../../../core/services/auth';
import { Incidente, IncidenteService } from '../../../../core/services/incidente.service';
import { Severidad, SeveridadService } from '../../../../core/services/severidad.service';
import { TipoIncidente, TipoIncidenteService } from '../../../../core/services/tipo-incidente.service';
import { TurnoService, TurnoBackend } from '../../../../core/services/turno.service';
import { Zona, ZonaService } from '../../../../core/services/zona.service';

const SEVERITY_COLORS: Record<string, string> = {
	S1: 'bg-amber-500',
	S2: 'bg-orange-500',
	S3: 'bg-rose-500',
};

const TYPE_COLORS = ['bg-rose-500', 'bg-amber-500', 'bg-sky-500', 'bg-violet-500', 'bg-emerald-500', 'bg-indigo-500'];

const HOUR_TONES = [
	'bg-emerald-50 border-emerald-200 text-emerald-700',
	'bg-sky-50 border-sky-200 text-sky-700',
	'bg-amber-50 border-amber-200 text-amber-700',
	'bg-rose-50 border-rose-200 text-rose-700',
];

@Component({
	selector: 'app-coordinator-analytics',
	imports: [CommonModule, FormsModule],
	templateUrl: './analytics.html',
	styleUrl: './analytics.css',
})
export class CoordinatorAnalytics implements OnInit, OnDestroy {
	private readonly router          = inject(Router);
	private readonly auth            = inject(Auth);
	private readonly incidenteSvc    = inject(IncidenteService);
	private readonly zonaSvc         = inject(ZonaService);
	private readonly severidadSvc    = inject(SeveridadService);
	private readonly tipoIncidenteSvc = inject(TipoIncidenteService);
	private readonly turnoSvc        = inject(TurnoService);

	protected readonly loading      = signal(true);
	protected readonly errorMessage = signal<string | null>(null);

	protected readonly incidents   = signal<Incidente[]>([]);
	protected readonly zonas       = signal<Zona[]>([]);
	protected readonly severidades = signal<Severidad[]>([]);
	protected readonly tipos       = signal<TipoIncidente[]>([]);
	protected readonly turnos      = signal<TurnoBackend[]>([]);

	protected readonly userName = signal<string>(this.auth.getUser()?.username ?? 'Coordinador');
	protected readonly today    = signal<string>(new Date().toISOString().slice(0, 10));

	protected readonly timeRange    = signal<'24h' | '7d' | '30d' | '90d'>('7d');
	protected readonly selectedZone = signal<string>('all');

	private sub: Subscription | null = null;

	ngOnInit(): void {
		const user = this.auth.getUser();
		if (!user) {
			void this.router.navigateByUrl('/login');
			return;
		}

		const role = (user.role ?? '').replace('ROLE_', '').toUpperCase();
		if (role !== 'COORDINADOR') {
			void this.router.navigateByUrl('/login');
			return;
		}

		this.loadData();
	}

	ngOnDestroy(): void {
		this.sub?.unsubscribe();
	}

	private loadData(): void {
		this.sub?.unsubscribe();
		this.loading.set(true);
		this.errorMessage.set(null);

		this.sub = forkJoin({
			incidents:   this.incidenteSvc.getIncidentes(),
			zonas:       this.zonaSvc.getZonas(),
			severidades: this.severidadSvc.getSeveridades(),
			tipos:       this.tipoIncidenteSvc.getTipos(),
			turnos:      this.turnoSvc.getTurnos(),
		}).subscribe({
			next: ({ incidents, zonas, severidades, tipos, turnos }) => {
				this.incidents.set(incidents);
				this.zonas.set(zonas);
				this.severidades.set(severidades);
				this.tipos.set(tipos);
				this.turnos.set(turnos);
				this.loading.set(false);
			},
			error: (err) => {
				console.error('Error cargando datos de reportes:', err);
				this.errorMessage.set('No se pudo conectar con el servidor.');
				this.loading.set(false);
			},
		});
	}

	retryLoad(): void {
		this.loadData();
	}

	// ── Filtros y derivaciones ────────────────────────────────────────────

	private cutoffDate(): Date {
		const days = this.timeRange() === '24h' ? 1
			: this.timeRange() === '7d' ? 7
			: this.timeRange() === '30d' ? 30
			: 90;
		const cutoff = new Date();
		cutoff.setDate(cutoff.getDate() - days);
		return cutoff;
	}

	protected readonly filteredIncidents = computed(() => {
		const cutoff = this.cutoffDate();
		const zoneFilter = this.selectedZone();
		return this.incidents().filter(i => {
			const ts = new Date(i.fecha_hora);
			if (isNaN(ts.getTime()) || ts < cutoff) return false;
			if (zoneFilter !== 'all' && String(i.zona?.id_zona ?? '') !== zoneFilter) return false;
			return true;
		});
	});

	protected readonly totalIncidents = computed(() => this.filteredIncidents().length);

	protected readonly incidentsByZone = computed(() => {
		const filtered = this.filteredIncidents();
		const zonasList = this.selectedZone() === 'all'
			? this.zonas()
			: this.zonas().filter(z => String(z.id_zona) === this.selectedZone());

		const counts = zonasList.map(z => ({
			id:    String(z.id_zona),
			zone:  z.nombre,
			count: filtered.filter(i => i.zona?.id_zona === z.id_zona).length,
		}));
		const max = Math.max(...counts.map(c => c.count), 1);
		return counts.map(c => ({ ...c, max }));
	});

	protected readonly incidentTypes = computed(() => {
		const filtered = this.filteredIncidents();
		const map = new Map<string, number>();
		filtered.forEach(i => {
			const label = i.tipoIncidente?.nombre ?? 'Sin clasificar';
			map.set(label, (map.get(label) ?? 0) + 1);
		});
		return Array.from(map.entries()).map(([label, value], idx) => ({
			label,
			value,
			color: TYPE_COLORS[idx % TYPE_COLORS.length],
		}));
	});

	protected readonly severityBreakdown = computed(() => {
		const filtered = this.filteredIncidents();
		const total = filtered.length || 1;
		const map = new Map<string, number>();
		filtered.forEach(i => {
			const code = (i.severidad?.codigo ?? 'N/A').toUpperCase();
			map.set(code, (map.get(code) ?? 0) + 1);
		});
		const ordered = Array.from(map.entries()).sort((a, b) => a[0].localeCompare(b[0]));
		return ordered.map(([code, value]) => ({
			label:   `${code} - ${this.severityLabel(code)}`,
			value,
			percent: Math.round((value / total) * 100),
			color:   SEVERITY_COLORS[code] ?? 'bg-slate-400',
		}));
	});

	private severityLabel(code: string): string {
		const desc = this.severidades().find(s => s.codigo === code)?.descripcion;
		if (desc) return desc;
		if (code === 'S1') return 'Bajo';
		if (code === 'S2') return 'Medio';
		if (code === 'S3') return 'Alto';
		return code;
	}

	protected readonly peakHours = computed(() => {
		const filtered = this.filteredIncidents();
		const counts = new Map<number, number>();
		filtered.forEach(i => {
			const hour = new Date(i.fecha_hora).getHours();
			if (isNaN(hour)) return;
			counts.set(hour, (counts.get(hour) ?? 0) + 1);
		});
		const top = Array.from(counts.entries())
			.sort((a, b) => b[1] - a[1])
			.slice(0, 4)
			.sort((a, b) => a[0] - b[0]);
		return top.map(([hour, incidents], idx) => ({
			label:     `${String(hour).padStart(2, '0')}:00`,
			incidents,
			tone:      HOUR_TONES[idx % HOUR_TONES.length],
		}));
	});

	protected readonly compliance = computed(() => {
		const today = this.today();
		const todayShifts = this.turnos().filter(t => t.fecha === today);
		if (todayShifts.length === 0) return 'N/D';
		const covered = todayShifts.filter(t => t.estado?.toUpperCase() !== 'AUSENTE').length;
		return `${Math.round((covered / todayShifts.length) * 100)}%`;
	});

	protected readonly riskZones = computed(() => {
		const filtered = this.filteredIncidents();
		const byZone = new Map<number, number>();
		filtered.forEach(i => {
			if (i.zona?.id_zona == null) return;
			byZone.set(i.zona.id_zona, (byZone.get(i.zona.id_zona) ?? 0) + 1);
		});
		return Array.from(byZone.values()).filter(c => c >= 3).length;
	});

	protected readonly responseTime = computed(() => 'N/D');

	// ── Handlers ──────────────────────────────────────────────────────────

	setTimeRange(value: string): void {
		if (value === '24h' || value === '7d' || value === '30d' || value === '90d') {
			this.timeRange.set(value);
		}
	}

	setSelectedZone(value: string): void {
		this.selectedZone.set(value);
	}

	goBack(): void {
		void this.router.navigateByUrl('/coordinator');
	}

	exportReports(): void {
		console.log('Exportando reporte del coordinador');
	}

	trackById(_: number, item: { id: string }): string {
		return item.id;
	}

	trackByLabel(_: number, item: { label: string }): string {
		return item.label;
	}
}
