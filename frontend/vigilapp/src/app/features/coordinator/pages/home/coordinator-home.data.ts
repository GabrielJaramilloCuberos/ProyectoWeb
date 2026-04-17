import {
  CoordinatorIncident,
  CoordinatorShift,
  CoordinatorTeacher,
  CoordinatorUser,
  CoordinatorZone,
} from './coordinator-home.models';

export const COORDINATOR_DAY = '2026-02-26';

export const COORDINATOR_USER: CoordinatorUser = {
  name: 'Coordinador Operativo',
  role: 'coordinator'
};

export const MOCK_TEACHERS: CoordinatorTeacher[] = [
  { id: 't1', name: 'Laura Peña', department: 'Ciencias Sociales', isActive: true },
  { id: 't2', name: 'Andrés Mora', department: 'Matemáticas', isActive: true },
  { id: 't3', name: 'Sofía Rojas', department: 'Lengua', isActive: true },
  { id: 't4', name: 'Miguel Torres', department: 'Tecnología', isActive: true },
  { id: 't5', name: 'Paula García', department: 'Orientación', isActive: false }
];

export const MOCK_ZONES: CoordinatorZone[] = [
  {
    id: 'z1',
    name: 'Bloque A',
    description: 'Ingreso principal y pasillos administrativos.',
    capacity: 180,
    checkpoints: ['Entrada principal', 'Recepción', 'Pasillo norte']
  },
  {
    id: 'z2',
    name: 'Bloque B',
    description: 'Aulas de ciencias y laboratorios.',
    capacity: 220,
    checkpoints: ['Laboratorio 1', 'Laboratorio 2', 'Escaleras este']
  },
  {
    id: 'z3',
    name: 'Patio Central',
    description: 'Zona de recreo y circulación de estudiantes.',
    capacity: 320,
    checkpoints: ['Cancha', 'Bancas centrales', 'Salida oeste']
  },
  {
    id: 'z4',
    name: 'Bloque C',
    description: 'Biblioteca, sala de docentes y apoyo académico.',
    capacity: 150,
    checkpoints: ['Biblioteca', 'Sala docente', 'Acceso sur']
  }
];

export const MOCK_SHIFTS: CoordinatorShift[] = [
  {
    id: 's1',
    zoneId: 'z1',
    zoneName: 'Bloque A',
    teacherId: 't1',
    teacherName: 'Laura Peña',
    startTime: '07:00',
    endTime: '09:00',
    status: 'active',
    patrolCount: 4,
    date: COORDINATOR_DAY
  },
  {
    id: 's2',
    zoneId: 'z2',
    zoneName: 'Bloque B',
    teacherId: 't2',
    teacherName: 'Andrés Mora',
    startTime: '08:00',
    endTime: '10:00',
    status: 'active',
    patrolCount: 3,
    date: COORDINATOR_DAY
  },
  {
    id: 's3',
    zoneId: 'z3',
    zoneName: 'Patio Central',
    teacherId: 't3',
    teacherName: 'Sofía Rojas',
    startTime: '09:00',
    endTime: '11:00',
    status: 'missed',
    date: COORDINATOR_DAY
  },
  {
    id: 's4',
    zoneId: 'z4',
    zoneName: 'Bloque C',
    teacherId: 't4',
    teacherName: 'Miguel Torres',
    startTime: '10:00',
    endTime: '12:00',
    status: 'completed',
    patrolCount: 5,
    date: COORDINATOR_DAY
  }
];

export const MOCK_INCIDENTS: CoordinatorIncident[] = [
  {
    id: 'i0',
    zoneId: 'z1',
    zoneName: 'Bloque A',
    teacherName: 'Laura Peña',
    timestamp: '2026-01-22T08:25:00',
    severity: 'S1',
    description: 'Revisión preventiva en acceso principal.'
  },
  {
    id: 'i1',
    zoneId: 'z2',
    zoneName: 'Bloque B',
    teacherName: 'Andrés Mora',
    timestamp: '2026-02-26T08:15:00',
    severity: 'S2',
    description: 'Estudiantes reunidos fuera del laboratorio.'
  },
  {
    id: 'i1b',
    zoneId: 'z2',
    zoneName: 'Bloque B',
    teacherName: 'Andrés Mora',
    timestamp: '2026-02-21T11:10:00',
    severity: 'S1',
    description: 'Circulación controlada en pasillo de laboratorios.'
  },
  {
    id: 'i2',
    zoneId: 'z3',
    zoneName: 'Patio Central',
    teacherName: 'Sofía Rojas',
    timestamp: '2026-02-26T09:40:00',
    severity: 'S3',
    description: 'No se registró check-in dentro del tiempo esperado.'
  },
  {
    id: 'i2b',
    zoneId: 'z3',
    zoneName: 'Patio Central',
    teacherName: 'Sofía Rojas',
    timestamp: '2026-02-24T12:05:00',
    severity: 'S2',
    description: 'Aglomeración temporal en la salida oeste.'
  },
  {
    id: 'i3',
    zoneId: 'z1',
    zoneName: 'Bloque A',
    teacherName: 'Laura Peña',
    timestamp: '2026-02-26T10:05:00',
    severity: 'S1',
    description: 'Acceso controlado sin novedades.'
  },
  {
    id: 'i3b',
    zoneId: 'z1',
    zoneName: 'Bloque A',
    teacherName: 'Laura Peña',
    timestamp: '2026-02-25T13:35:00',
    severity: 'S2',
    description: 'Verificación de flujo en recepción.'
  },
  {
    id: 'i4',
    zoneId: 'z4',
    zoneName: 'Bloque C',
    teacherName: 'Miguel Torres',
    timestamp: '2026-01-30T16:45:00',
    severity: 'S1',
    description: 'Cierre ordenado de biblioteca.'
  },
  {
    id: 'i4b',
    zoneId: 'z4',
    zoneName: 'Bloque C',
    teacherName: 'Miguel Torres',
    timestamp: '2026-02-18T09:15:00',
    severity: 'S2',
    description: 'Apoyo académico con supervisión reforzada.'
  }
];

export function getCurrentUser(): CoordinatorUser | null {
  if (typeof localStorage === 'undefined') {
    return COORDINATOR_USER;
  }

  const rawUser = localStorage.getItem('auth_user');
  if (!rawUser) {
    return COORDINATOR_USER;
  }

  try {
    const parsed = JSON.parse(rawUser) as Partial<CoordinatorUser> & { role?: string };
    if (parsed.role === 'coordinator') {
      return {
        name: parsed.name || COORDINATOR_USER.name,
        role: 'coordinator'
      };
    }
  } catch {
    return COORDINATOR_USER;
  }

  return COORDINATOR_USER;
}