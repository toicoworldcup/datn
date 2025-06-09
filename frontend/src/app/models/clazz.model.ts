export interface Clazz {
  maLop: string;
  maHocPhan: string;
  hocki: string;
  lichThi?: string | null;
  teachers?: any[]; // Sử dụng khi lấy danh sách lớp (API trả về)
  maGv?: string;      // Sử dụng khi tạo mới lớp (form nhập liệu)
  soLuongSinhVien?: number; // Thêm thuộc tính này
}