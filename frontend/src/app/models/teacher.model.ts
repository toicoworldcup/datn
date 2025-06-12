// src/app/models/department.model.ts (Bạn có thể cần file này nếu bạn từng lấy danh sách khoa trực tiếp)
// Nếu Department không được sử dụng trực tiếp trong các model frontend, bạn có thể bỏ qua file này.
// export interface Department {
//   id?: number;
//   name: string;
// }

// src/app/models/teacher.model.ts
export interface Teacher {
  id?: number; // Tùy chọn vì có thể không có khi tạo mới hoặc một số DTO có thể bỏ qua nó.
  maGv: string;
  name: string;
  email: string;
  phone: string; // <--- Đã sửa thành phoneNumber để nhất quán
  dateOfBirth: string;
  address: string;
  departmentName: string; // <--- Đây phải là một chuỗi, vì DTO backend của bạn cung cấp nó.
  gender: string;
  cccd: string;
  // Nếu DTO backend của bạn cho các hoạt động GET *cũng* bao gồm một đối tượng Department đầy đủ,
  // thì bạn sẽ thêm: department?: Department; nhưng dựa trên DTO của bạn, nó chỉ có departmentName.
}