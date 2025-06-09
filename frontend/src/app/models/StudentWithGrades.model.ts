export interface StudentWithGrades {
  maLop: string;
  semesterName: string;
  name: string;
  mssv: string;
  diemGk?: number | null;
  diemCk?: number | null;
  history?: string; // Thêm trường history
}