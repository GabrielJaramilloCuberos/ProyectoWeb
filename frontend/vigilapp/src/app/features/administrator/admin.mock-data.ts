import { AuditLog, Incident, LeaderboardEntry, Shift, Teacher, Zone } from './services/admin.service';

const toUtcDate = (offsetDays = 0): string => {
	const date = new Date();
	date.setUTCDate(date.getUTCDate() + offsetDays);
	return date.toISOString().split('T')[0];
};

const toUtcTimestamp = (offsetDays: number, hours: number, minutes: number): string => {
	const date = new Date();
	date.setUTCDate(date.getUTCDate() + offsetDays);
	date.setUTCHours(hours, minutes, 0, 0);
	return date.toISOString();
};

export const MOCK_ZONES: Zone[] = [
	{
		id: 'zone-1',
		name: 'Parque Bachillerato Izquierdo',
		description: 'Área abierta de vigilancia para el bloque de bachillerato izquierdo.',
		checkpoints: ['Entrada norte', 'Borde occidental', 'Acceso a pasillo B'],
		isActive: true,
	},
	{
		id: 'zone-2',
		name: 'Parque Bachillerato Central',
		description: 'Zona central de tránsito entre aulas y patios internos.',
		checkpoints: ['Acceso principal', 'Pasillo central', 'Punto de cruce'],
		isActive: true,
	},
	{
		id: 'zone-3',
		name: 'Parque Preescolar',
		description: 'Sector protegido para ingreso y salida de primera infancia.',
		checkpoints: ['Puerta peatonal', 'Área de juego', 'Control de acompañamiento'],
		isActive: true,
	},
	{
		id: 'zone-4',
		name: 'Coliseo',
		description: 'Espacio cubierto para eventos, actos y actividades masivas.',
		checkpoints: ['Escenario', 'Acceso lateral', 'Salida posterior'],
		isActive: true,
	},
	{
		id: 'zone-5',
		name: 'Cancha de Baloncesto',
		description: 'Zona deportiva abierta con alta circulación en recreos.',
		checkpoints: ['Banca de equipos', 'Cancha central', 'Acceso sur'],
		isActive: true,
	},
	{
		id: 'zone-6',
		name: 'Cafetería Bachillerato',
		description: 'Punto de atención y flujo alto en horarios de descanso.',
		checkpoints: ['Caja', 'Ventana de entrega', 'Zona de mesas'],
		isActive: true,
	},
	{
		id: 'zone-7',
		name: 'Cafetería Preescolar',
		description: 'Área de apoyo para alimentación y control de menores.',
		checkpoints: ['Mesa de recepción', 'Acceso lateral', 'Apoyo docente'],
		isActive: false,
	},
];

export const MOCK_TEACHERS: Teacher[] = [
	{ id: 'teacher-1', name: 'Sofía Rojas', department: 'Preescolar', isActive: true },
	{ id: 'teacher-2', name: 'Carlos Méndez', department: 'Matemáticas', isActive: true },
	{ id: 'teacher-3', name: 'Laura Gómez', department: 'Convivencia', isActive: true },
	{ id: 'teacher-4', name: 'Andrés Pineda', department: 'Educación Física', isActive: true },
	{ id: 'teacher-5', name: 'Mariana Torres', department: 'Humanidades', isActive: true },
	{ id: 'teacher-6', name: 'Julián Herrera', department: 'Aseo y Apoyo', isActive: false },
];

export const MOCK_SHIFTS: Shift[] = [
	{
		id: 'shift-1',
		teacherId: 'teacher-1',
		teacherName: 'Sofía Rojas',
		zoneId: 'zone-3',
		zoneName: 'Parque Preescolar',
		date: toUtcDate(),
		startTime: '07:00',
		endTime: '09:00',
		status: 'completed',
		checkInTime: '06:58',
	},
	{
		id: 'shift-2',
		teacherId: 'teacher-2',
		teacherName: 'Carlos Méndez',
		zoneId: 'zone-2',
		zoneName: 'Parque Bachillerato Central',
		date: toUtcDate(),
		startTime: '09:00',
		endTime: '11:00',
		status: 'active',
		checkInTime: '08:55',
	},
	{
		id: 'shift-3',
		teacherId: 'teacher-3',
		teacherName: 'Laura Gómez',
		zoneId: 'zone-4',
		zoneName: 'Coliseo',
		date: toUtcDate(),
		startTime: '10:00',
		endTime: '12:00',
		status: 'pending',
	},
	{
		id: 'shift-4',
		teacherId: 'teacher-4',
		teacherName: 'Andrés Pineda',
		zoneId: 'zone-5',
		zoneName: 'Cancha de Baloncesto',
		date: toUtcDate(),
		startTime: '12:00',
		endTime: '14:00',
		status: 'missed',
	},
	{
		id: 'shift-5',
		teacherId: 'teacher-5',
		teacherName: 'Mariana Torres',
		zoneId: 'zone-1',
		zoneName: 'Parque Bachillerato Izquierdo',
		date: toUtcDate(1),
		startTime: '08:00',
		endTime: '10:00',
		status: 'pending',
	},
	{
		id: 'shift-6',
		teacherId: 'teacher-1',
		teacherName: 'Sofía Rojas',
		zoneId: 'zone-6',
		zoneName: 'Cafetería Bachillerato',
		date: toUtcDate(2),
		startTime: '11:30',
		endTime: '13:30',
		status: 'pending',
	},
];

export const MOCK_AUDIT_LOGS: AuditLog[] = [
	{
		id: 'audit-1',
		action: 'Turno completado',
		details: 'Sofía Rojas cerró el turno de Parque Preescolar con check-in correcto.',
		role: 'Docente',
		user: 'Sofía Rojas',
		timestamp: toUtcTimestamp(0, 10, 5),
	},
	{
		id: 'audit-2',
		action: 'Incidente reportado',
		details: 'Se registró un incidente leve en el Coliseo durante la jornada de la mañana.',
		role: 'Docente',
		user: 'Carlos Méndez',
		timestamp: toUtcTimestamp(0, 10, 25),
	},
	{
		id: 'audit-3',
		action: 'Zona actualizada',
		details: 'Se ajustaron los puntos de control de Cafetería Bachillerato.',
		role: 'Administrador',
		user: 'Administrador',
		timestamp: toUtcTimestamp(-1, 15, 40),
	},
	{
		id: 'audit-4',
		action: 'Turno reasignado',
		details: 'El turno de la Cancha de Baloncesto fue reasignado por ausencia.',
		role: 'Coordinador',
		user: 'Coordinación General',
		timestamp: toUtcTimestamp(-1, 17, 10),
	},
	{
		id: 'audit-5',
		action: 'Configuración guardada',
		details: 'Se actualizaron minutos de notificación y reglas de gamificación.',
		role: 'Administrador',
		user: 'Administrador',
		timestamp: toUtcTimestamp(-2, 9, 15),
	},
];

export const MOCK_INCIDENTS: Incident[] = [
	{
		id: 'incident-1',
		severity: 'S1',
		type: 'space',
		description: 'Congestión temporal en la salida del bloque central.',
		zoneName: 'Parque Bachillerato Central',
		teacherName: 'Carlos Méndez',
		resolved: true,
		timestamp: toUtcTimestamp(0, 9, 50),
	},
	{
		id: 'incident-2',
		severity: 'S2',
		type: 'coexistence',
		description: 'Discusión breve entre estudiantes en la zona deportiva.',
		zoneName: 'Cancha de Baloncesto',
		teacherName: 'Andrés Pineda',
		resolved: false,
		timestamp: toUtcTimestamp(0, 11, 12),
	},
	{
		id: 'incident-3',
		severity: 'S3',
		type: 'physical',
		description: 'Caída menor sin lesiones en el acceso al coliseo.',
		zoneName: 'Coliseo',
		teacherName: 'Laura Gómez',
		resolved: true,
		timestamp: toUtcTimestamp(-1, 14, 5),
	},
	{
		id: 'incident-4',
		severity: 'S1',
		type: 'social',
		description: 'Aislamiento de un estudiante durante el almuerzo.',
		zoneName: 'Cafetería Bachillerato',
		teacherName: 'Mariana Torres',
		resolved: false,
		timestamp: toUtcTimestamp(-1, 12, 45),
	},
];

export const MOCK_LEADERBOARD: LeaderboardEntry[] = [
	{
		teacherId: 'teacher-1',
		teacherName: 'Sofía Rojas',
		department: 'Preescolar',
		points: 1240,
		shiftsCompleted: 18,
		punctualityRate: 98,
		patrolsTotal: 36,
		incidentsReported: 2,
		badges: 5,
	},
	{
		teacherId: 'teacher-2',
		teacherName: 'Carlos Méndez',
		department: 'Matemáticas',
		points: 1180,
		shiftsCompleted: 17,
		punctualityRate: 96,
		patrolsTotal: 34,
		incidentsReported: 3,
		badges: 4,
	},
	{
		teacherId: 'teacher-3',
		teacherName: 'Laura Gómez',
		department: 'Convivencia',
		points: 1050,
		shiftsCompleted: 15,
		punctualityRate: 94,
		patrolsTotal: 28,
		incidentsReported: 4,
		badges: 3,
	},
	{
		teacherId: 'teacher-4',
		teacherName: 'Andrés Pineda',
		department: 'Educación Física',
		points: 980,
		shiftsCompleted: 14,
		punctualityRate: 92,
		patrolsTotal: 26,
		incidentsReported: 1,
		badges: 3,
	},
	{
		teacherId: 'teacher-5',
		teacherName: 'Mariana Torres',
		department: 'Humanidades',
		points: 905,
		shiftsCompleted: 13,
		punctualityRate: 90,
		patrolsTotal: 24,
		incidentsReported: 2,
		badges: 2,
	},
];