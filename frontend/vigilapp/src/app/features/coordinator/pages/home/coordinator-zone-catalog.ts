export type CoordinatorZoneCatalogItem = {
	id: string;
	reportId: number;
	name: string;
	description: string;
	capacity: number;
	checkpoints: string[];
	accidentCount: number;
	points: [number, number][];
};

export const COORDINATOR_ZONE_CATALOG: CoordinatorZoneCatalogItem[] = [
	{
		id: 'z1',
		reportId: 1,
		name: 'Parque Bachillerato Izquierdo',
		description: 'Zona oeste del bachillerato con control de ingreso y circulación.',
		capacity: 180,
		checkpoints: ['Acceso oeste', 'Pasillo administrativo', 'Salida lateral'],
		accidentCount: 3,
		points: [
			[4.733541, -74.042525],
			[4.733514, -74.042429],
			[4.732600, -74.042587],
			[4.732592, -74.042740],
		],
	},
	{
		id: 'z2',
		reportId: 2,
		name: 'Parque Bachillerato central',
		description: 'Corazón del área de bachillerato con tránsito alto de estudiantes.',
		capacity: 220,
		checkpoints: ['Acceso central', 'Patio medio', 'Corredor norte'],
		accidentCount: 2,
		points: [
			[4.733333, -74.041898],
			[4.733305, -74.041814],
			[4.733081, -74.041651],
			[4.732696, -74.041736],
			[4.732633, -74.041821],
			[4.732811, -74.041944],
		],
	},
	{
		id: 'z3',
		reportId: 3,
		name: 'Parque preescolar',
		description: 'Sector de preescolar con circulación controlada y acceso reducido.',
		capacity: 320,
		checkpoints: ['Ingreso preescolar', 'Zona de juego', 'Salida segura'],
		accidentCount: 2,
		points: [
			[4.733081, -74.041329],
			[4.732997, -74.040983],
			[4.732449, -74.041150],
			[4.732536, -74.041600],
			[4.733050, -74.041491],
			[4.733026, -74.041338],
		],
	},
	{
		id: 'z4',
		reportId: 4,
		name: 'Coliseo',
		description: 'Espacio cerrado para actividades múltiples y eventos institucionales.',
		capacity: 150,
		checkpoints: ['Entrada principal', 'Graderías', 'Acceso técnico'],
		accidentCount: 2,
		points: [
			[4.733415, -74.041596],
			[4.733355, -74.041279],
			[4.733028, -74.041366],
			[4.733090, -74.041666],
		],
	},
	{
		id: 'z5',
		reportId: 5,
		name: 'Cancha baloncesto',
		description: 'Área deportiva abierta con recorridos frecuentes en horas pico.',
		capacity: 240,
		checkpoints: ['Borde norte', 'Borde sur', 'Acceso deportivo'],
		accidentCount: 1,
		points: [
			[4.733388, -74.041263],
			[4.733347, -74.041086],
			[4.733048, -74.041147],
			[4.733090, -74.041331],
		],
	},
	{
		id: 'z6',
		reportId: 6,
		name: 'Cafeteria Bachillerato',
		description: 'Punto de alimentación del bachillerato con afluencia media-baja.',
		capacity: 160,
		checkpoints: ['Fila principal', 'Mesas centrales', 'Salida lateral'],
		accidentCount: 1,
		points: [
			[4.733230, -74.042374],
			[4.733167, -74.042031],
			[4.733050, -74.042053],
			[4.733120, -74.042394],
		],
	},
	{
		id: 'z7',
		reportId: 7,
		name: 'Cafeteria Preescolar',
		description: 'Área de cafetería para preescolar con control de acompañamiento.',
		capacity: 120,
		checkpoints: ['Ingreso', 'Zona de atención', 'Salida acompañada'],
		accidentCount: 1,
		points: [
			[4.732798, -74.041685],
			[4.732778, -74.041604],
			[4.732574, -74.041633],
			[4.732578, -74.041712],
		],
	},
];

export const REPORT_ZONE_OPTIONS = COORDINATOR_ZONE_CATALOG.map(zone => ({
	id: zone.reportId,
	nombre: zone.name,
	descripcion: zone.description,
}));

export const HEATMAP_POLYGONS = COORDINATOR_ZONE_CATALOG.map(zone => ({
	id: zone.id,
	nombre: zone.name,
	accidentCount: zone.accidentCount,
	points: zone.points,
}));