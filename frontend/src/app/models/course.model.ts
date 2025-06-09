export interface Course {
  maHocPhan: string;
  soTinChi: number;
  tenMonHoc: string;
  khoiLuong: string;
  suggestedSemester?: number;
  finalGrade?: number | null;
  gradeLetter?: string | null;
}