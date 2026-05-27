import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

export type ToastKind = 'success' | 'info' | 'warning' | 'danger';

export type ToastItem = {
	id: number;
	title: string;
	message: string;
	kind: ToastKind;
};

@Component({
	selector: 'app-toast-stack',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './toast-stack.html',
	styleUrl: './toast-stack.css',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ToastStack {
	@Input() items: ToastItem[] = [];

	getToneClass(kind: ToastKind): string {
		switch (kind) {
			case 'success':
				return 'border-emerald-400/70';
			case 'warning':
				return 'border-amber-400/70';
			case 'danger':
				return 'border-rose-400/70';
			default:
				return 'border-sky-400/70';
		}
	}

	getBadgeClass(kind: ToastKind): string {
		switch (kind) {
			case 'success':
				return 'bg-emerald-500/20 text-emerald-100';
			case 'warning':
				return 'bg-amber-500/20 text-amber-100';
			case 'danger':
				return 'bg-rose-500/20 text-rose-100';
			default:
				return 'bg-sky-500/20 text-sky-100';
		}
	}

	getIcon(kind: ToastKind): string {
		switch (kind) {
			case 'success':
				return '✓';
			case 'warning':
				return '!';
			case 'danger':
				return '×';
			default:
				return 'i';
		}
	}
}