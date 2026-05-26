import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

type LegendItem = {
	label: string;
	description: string;
	color: string;
};

@Component({
	selector: 'app-heatmap-color-legend',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './heatmap-color-legend.html',
	styleUrl: './heatmap-color-legend.css',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeatmapColorLegend {
	protected readonly items: LegendItem[] = [
		{
			label: 'Baja',
			description: '0 a 1 accidentes',
			color: '#16a34a',
		},
		{
			label: 'Media',
			description: '2 accidentes',
			color: '#d97706',
		},
		{
			label: 'Alta',
			description: '3 a 4 accidentes',
			color: '#ea580c',
		},
		{
			label: 'Crítica',
			description: '5 accidentes o más',
			color: '#dc2626',
		},
	];
}