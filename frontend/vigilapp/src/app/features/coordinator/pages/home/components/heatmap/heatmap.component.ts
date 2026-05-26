import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnDestroy, PLATFORM_ID, ViewChild, inject } from '@angular/core';

import { HEATMAP_POLYGONS } from '../../coordinator-zone-catalog';
import { HeatmapColorLegend } from './heatmap-color-legend/heatmap-color-legend';

type ZoneMapMode = 'satellite' | 'simple';

type HeatmapZone = {
  nombre: string;
  accidentCount: number;
  puntos: [number, number][];
};

@Component({
  selector: 'app-heatmap',
  standalone: true,
  template: `
    <section class="space-y-4">
      <div class="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h2 class="px-1 text-sm font-semibold uppercase tracking-wider text-slate-500">Mapa de calor</h2>
          <p class="px-1 text-sm text-slate-600">Zonas de vigilancia con indicadores de intensidad (1-10).</p>
        </div>

        <div class="flex items-center gap-2 px-1 text-[11px] font-medium text-slate-500">
          <span class="rounded-full border border-green-300 bg-green-100 px-3 py-1 text-green-800">Low (1-3)</span>
          <span class="rounded-full border border-yellow-400 bg-yellow-100 px-3 py-1 text-yellow-800">Medium (4-7)</span>
          <span class="rounded-full border border-red-400 bg-red-100 px-3 py-1 text-red-800">High (8-9)</span>
          <span class="rounded-full border border-red-900 bg-red-900 px-2 py-1 text-white">Extreme (10)</span>
        </div>
      </div>

      <div class="flex justify-start px-1">
        <button
          type="button"
          class="inline-flex items-center justify-center rounded-full border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:border-slate-300 hover:text-slate-950"
          (click)="toggleBaseLayer()"
        >
          {{ mapMode === 'satellite' ? 'Ver mapa simple' : 'Ver satélite' }}
        </button>
      </div>

      <div class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm" style="position: relative; z-index: 0;">
        <div class="h-128 w-full" style="position: relative; z-index: 0;">
          <div #mapContainer class="h-full w-full" style="position: relative; z-index: auto;"></div>
        </div>
      </div>
    </section>
  `,
  host: {
    class: 'block w-full',
    style: 'position: relative; z-index: 0;',
  },
})
export class HeatmapComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapContainer') private readonly mapContainer?: ElementRef<HTMLDivElement>;

  private readonly platformId = inject(PLATFORM_ID);
  private readonly center: [number, number] = [4.7330, -74.0415];
  private readonly initialZoom = 19;
  private leaflet: any | null = null;
  private activeBaseLayer: any | null = null;

  protected mapMode: ZoneMapMode = 'satellite';

  private readonly zonasPolygon: HeatmapZone[] = HEATMAP_POLYGONS.map(zone => ({
    nombre: zone.nombre,
    accidentCount: zone.accidentCount,
    puntos: zone.points,
  }));

  private map: import('leaflet').Map | null = null;
  private polygons: any[] = [];

  async ngAfterViewInit(): Promise<void> {
    if (!isPlatformBrowser(this.platformId) || !this.mapContainer) {
      return;
    }

    const leafletModule = await import('leaflet');
    const L = leafletModule.default;
    this.leaflet = leafletModule.default;

    this.map = L.map(this.mapContainer.nativeElement, {
      center: this.center,
      zoom: this.initialZoom,
      minZoom: 17,
      maxZoom: 19,
      zoomControl: true,
      preferCanvas: true,
    });

    this.setBaseLayer(L, this.mapMode);

    // Crear polígonos de zonas de vigilancia
    this.zonasPolygon.forEach((zona) => {
      const color = this.getColorForAccidents(zona.accidentCount);
      console.log(`Zona: ${zona.nombre}, Numero de accidentes: ${zona.accidentCount}, Color: ${color}`);
      
      const polygon = L.polygon(zona.puntos, {
        color,
        weight: 2,
        opacity: 0.85,
        fillColor: color,
        fillOpacity: 0.45,
      }).addTo(this.map!);

      polygon.bindPopup(`
        <div class="text-sm font-medium">
          <p class="font-bold text-base">${zona.nombre}</p>
          <p class="text-xs text-gray-600 mt-1">Número de accidentes: <span class="font-semibold">${zona.accidentCount}</span></p>
        </div>
      `);

      this.polygons.push(polygon);
    });
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.polygons.forEach(polygon => polygon.remove());
      this.polygons = [];
      this.activeBaseLayer = null;
      
      this.map.remove();
      this.map = null;
      this.activeBaseLayer = null;
    }
  }

  toggleBaseLayer(): void {
    if (!this.map || !this.leaflet) {
      return;
    }

    this.mapMode = this.mapMode === 'satellite' ? 'simple' : 'satellite';
    this.setBaseLayer(this.leaflet, this.mapMode);
  }

  public addZonePolygon(zona: HeatmapZone): void {
    if (this.map) {
      const leaflet = (window as any).L;
      const color = this.getColorForAccidents(zona.accidentCount);

      const polygon = leaflet.polygon(zona.puntos, {
        color,
        weight: 2,
        opacity: 0.85,
        fillColor: color,
        fillOpacity: 0.45,
      }).addTo(this.map);

      polygon.bindPopup(`
        <div class="text-sm font-medium">
          <p class="font-bold text-base">${zona.nombre}</p>
          <p class="text-xs text-gray-600 mt-1">Número de accidentes: <span class="font-semibold">${zona.accidentCount}</span></p>
        </div>
      `);

      this.polygons.push(polygon);
    }
  }

  public updateAllZonePolygons(newZones: HeatmapZone[]): void {
    this.polygons.forEach(polygon => polygon.remove());
    this.polygons = [];

    if (this.map) {
      const leaflet = (window as any).L;
      newZones.forEach((zona) => {
        const color = this.getColorForAccidents(zona.accidentCount);

        const polygon = leaflet.polygon(zona.puntos, {
          color,
          weight: 2,
          opacity: 0.85,
          fillColor: color,
          fillOpacity: 0.35,
        }).addTo(this.map!);

        polygon.bindPopup(`
          <div class="text-sm font-medium">
            <p class="font-bold text-base">${zona.nombre}</p>
            <p class="text-xs text-gray-600 mt-1">Número de accidentes: <span class="font-semibold">${zona.accidentCount}</span></p>
          </div>
        `);

        this.polygons.push(polygon);
      });
    }
  }

  private setBaseLayer(L: any, mode: ZoneMapMode): void {
    if (!this.map) {
      return;
    }

    if (this.activeBaseLayer) {
      this.map.removeLayer(this.activeBaseLayer);
      this.activeBaseLayer = null;
    }

    this.activeBaseLayer = mode === 'satellite'
      ? L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
          attribution: 'Tiles &copy; Esri',
          maxZoom: 20,
          minZoom: 1,
        })
      : L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
          attribution: '&copy; OpenStreetMap contributors &copy; CARTO',
          maxZoom: 20,
          minZoom: 1,
        });

    this.activeBaseLayer.addTo(this.map);
  }

  private getColorForAccidents(accidentCount: number): string {
    if (accidentCount <= 1) {
      return '#16a34a';
    }

    if (accidentCount <= 2) {
      return '#d97706';
    }

    if (accidentCount <= 3) {
      return '#ea580c';
    }

    if (accidentCount <= 5) {
      return '#dc2626';
    }

    return '#7f1d1d';
  }

}