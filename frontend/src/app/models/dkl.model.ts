export interface Dkl {
  id?: number; // Thêm ID vào interface Dkl
  maLop?: string;
  semesterName?: string;
  // Add other relevant properties if needed based on DklDTO
}

export interface DklRequest {
  maLop: string;
  semesterName: string;
}

export interface DklDTO {
  id?: number; // Thêm ID vào interface DklDTO
  maLop?: string;
  semesterName?: string;
  mssv?: string; // Thêm mssv nếu bạn cần
}