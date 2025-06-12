export interface Course {
  id?: number; // Thêm trường ID, làm optional vì nó không có khi tạo mới
  maHocPhan: string;
  soTinChi: number;
  tenMonHoc: string;
  khoiLuong: string;
  suggestedSemester?: number;
  finalGrade?: number | null; // Giữ nguyên, đã có optional
  gradeLetter?: string | null; // Giữ nguyên, đã có optional
  gradeRatio: string;
}