import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';
import { ClazzService } from '../../services/clazz.service';
import { Clazz } from '../../models/clazz.model';

@Component({
  selector: 'app-clazz-list',
  templateUrl: './clazz-list.component.html',
  styleUrls: ['./clazz-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class ClazzListComponent implements OnInit {
  clazzes: Clazz[] = [];
  p: number = 1;

  // Form data
  newClazz: Partial<Clazz> = {};
  editLichThi: Date | null = null;
  assignMaGv: string = '';
  assignHocKi: string = '';
  assignMaLop: string = '';
  isAddClazzModalVisible = false; // Mặc định là ẩn phần tìm kiếm


  constructor(private clazzService: ClazzService) {}

  ngOnInit(): void {
    this.loadClazzes();
  }
  toggleAddClazzModal() {
    this.isAddClazzModalVisible = !this.isAddClazzModalVisible; // Chuyển đổi trạng thái hiển thị
  }

  loadClazzes(): void {
    this.clazzService.getAllClazzes().subscribe({
      next: (data) => (this.clazzes = data),
      error: (err) => console.error('Lỗi khi tải lớp:', err),
    });
  }

  addClazz(): void {
    if (!this.newClazz.maLop || !this.newClazz.hocki) {
      alert('Vui lòng nhập đầy đủ mã lớp và học kỳ.');
      return;
    }

    this.clazzService.addClazz(this.newClazz as Clazz).subscribe({
      next: (clazz) => {
        this.clazzes.push(clazz);
        this.newClazz = {}; // clear form
      },
      error: (err) => console.error('Lỗi khi thêm lớp:', err),
    });
  }

  assignTeacher(): void {
    if (!this.assignMaLop || !this.assignHocKi || !this.assignMaGv) {
      alert('Vui lòng nhập đầy đủ thông tin gán giáo viên.');
      return;
    }

    this.clazzService
      .assignTeacherToClazz(this.assignMaLop, this.assignHocKi, this.assignMaGv)
      .subscribe({
        next: (msg) => {
          alert('Đã gán giáo viên thành công.');
          this.loadClazzes();
          this.assignMaGv = '';
        },
        error: (err) => console.error('Lỗi khi gán giáo viên:', err),
      });
  }
}
