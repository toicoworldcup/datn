import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { SpecialClassRequestDTO } from '../../../models/SpecialClassRequestDTO.model';
import { DangKiLopService } from '../../../services/dangkilop.service';


@Component({
  selector: 'app-dangkilop',
  templateUrl: "./dangkilop.component.html",
  styleUrls: ["./dangkilop.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
    
})
export class DangkilopComponent implements OnInit {
  pendingRequests: SpecialClassRequestDTO[] = [];
  message: string = '';
  error: string = '';
    p: number = 1;


  constructor(private dangKiLopService: DangKiLopService) { }

  ngOnInit(): void {
    this.loadPendingRequests();
  }

  loadPendingRequests(): void {
    this.dangKiLopService.getPendingSpecialRequests().subscribe({
      next: (data) => {
        this.pendingRequests = data.map(req => ({ ...req, isSelected: false }));
      },
      error: (err) => {
        this.error = 'Lỗi khi tải yêu cầu: ' + err;
      }
    });
  }

  approveRequest(requestId: number | undefined): void {
    if (requestId) {
      this.dangKiLopService.approveSpecialRequest(requestId).subscribe({
        next: (response) => {
          this.message = response;
          this.error = '';
          this.loadPendingRequests(); // Tải lại danh sách sau khi duyệt
        },
        error: (err) => {
          this.error = 'Lỗi khi duyệt yêu cầu ID ' + requestId + ': ' + err;
          this.message = '';
        }
      });
    }
  }

  rejectRequest(requestId: number | undefined): void {
    if (requestId) {
      this.dangKiLopService.rejectSpecialRequest(requestId).subscribe({
        next: (response) => {
          this.message = response;
          this.error = '';
          this.loadPendingRequests(); // Tải lại danh sách sau khi từ chối
        },
        error: (err) => {
          this.error = 'Lỗi khi từ chối yêu cầu ID ' + requestId + ': ' + err;
          this.message = '';
        }
      });
    }
  }

  getSelectedRequestIds(): number[] {
    return this.pendingRequests.filter(req => req.isSelected && req.id !== undefined).map(req => req.id as number);
  }

  selectedRequestsExist(): boolean {
    return this.getSelectedRequestIds().length > 0;
  }

  approveSelectedRequests(): void {
    const selectedIds = this.getSelectedRequestIds();
    if (selectedIds.length > 0) {
      this.dangKiLopService.approveMultipleSpecialRequests(selectedIds).subscribe({
        next: (response) => {
          this.message = response;
          this.error = '';
          this.loadPendingRequests(); // Tải lại danh sách sau khi duyệt hàng loạt
        },
        error: (err) => {
          this.error = 'Lỗi khi duyệt các yêu cầu đã chọn: ' + err;
          this.message = '';
        }
      });
    } else {
      this.message = 'Vui lòng chọn các yêu cầu để duyệt.';
      this.error = '';
    }
  }

  rejectSelectedRequests(): void {
    const selectedIds = this.getSelectedRequestIds();
    if (selectedIds.length > 0) {
      this.dangKiLopService.rejectMultipleSpecialRequests(selectedIds).subscribe({
        next: (response) => {
          this.message = response;
          this.error = '';
          this.loadPendingRequests(); // Tải lại danh sách sau khi từ chối hàng loạt
        },
        error: (err) => {
          this.error = 'Lỗi khi từ chối các yêu cầu đã chọn: ' + err;
          this.message = '';
        }
      });
    } else {
      this.message = 'Vui lòng chọn các yêu cầu để từ chối.';
      this.error = '';
    }
  }
}