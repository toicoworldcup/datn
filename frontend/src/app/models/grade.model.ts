export interface Grade {
  mssv: string;
  maLop: string;
  semesterName: string;
  gki: number | null;
  cki: number | null;
  history?: string; // Thêm thuộc tính history (optional)
}