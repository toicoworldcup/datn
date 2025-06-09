export interface Attendance {
  status: string; // Ví dụ: 'PRESENT', 'ABSENT'
  attendanceDate: string; // Kiểu string để khớp với LocalDate trên backend
  maLop?: string | null;
  mssv?: string | null;
  hocKi?: string | null;
}