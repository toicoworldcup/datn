export interface SpecialClassRequestDTO {
  id?: number;
  requestDate?: string;
  status?: string;
  studentMssv?: string;
  studentName?: string;
  clazzMaLop?: string;
  semesterName?: string;
  isSelected?: boolean; // Thêm thuộc tính isSelected
}