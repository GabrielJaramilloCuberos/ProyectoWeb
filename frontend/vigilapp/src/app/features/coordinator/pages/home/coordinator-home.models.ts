export type ShiftStatus = 'active' | 'missed' | 'completed' | 'pending';

export type IncidentSeverity = 'S1' | 'S2' | 'S3';

export type CoordinatorUser = {
  name: string;
  role: 'coordinator';
};

export type CoordinatorTeacher = {
  id: string;
  name: string;
  department: string;
  isActive: boolean;
};

export type CoordinatorZone = {
  id: string;
  name: string;
  description: string;
  capacity: number;
  checkpoints: string[];
};

export type CoordinatorShift = {
  id: string;
  zoneId: string;
  zoneName: string;
  teacherId: string;
  teacherName: string;
  startTime: string;
  endTime: string;
  status: ShiftStatus;
  patrolCount?: number;
  date: string;
};

export type CoordinatorIncident = {
  id: string;
  zoneId: string;
  zoneName: string;
  teacherName: string;
  timestamp: string;
  severity: IncidentSeverity;
  description: string;
};