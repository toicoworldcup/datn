// src/app/components/header/header.component.ts
import { Component, OnInit } from "@angular/core";
import {
  StudentInfoResponse,
  StudentService,
} from "../services/student.service";
import {
  TeacherInfoResponse,
  TeacherService,
} from "../services/teacher.service";
import { CommonModule, NgIf } from "@angular/common"; // Import NgIf
import { AuthService } from "../core/services/auth.service";
import { Router } from "express";

@Component({
  selector: "app-header",
  standalone: true,

  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.css"],
  imports: [CommonModule], // 👈 BẮT BUỘC PHẢI CÓ
})
export class HeaderComponent implements OnInit {
  // Đảm bảo implements OnInit
  username: string | null = null;
  isLoggedIn: boolean = false;
  role: string = "";
  isDropdownOpen: boolean = false;

  constructor() {}

  // Định nghĩa ngOnInit()
  ngOnInit(): void {
    if (typeof window !== "undefined") {
      this.username = localStorage.getItem("username");
      this.role = localStorage.getItem("role") || "";
      this.isLoggedIn = !!this.username;
    }
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  login() {
    window.location.href = "/login";
  }

  logout() {
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    this.username = null;
    this.role = "";
    this.isLoggedIn = false;
    window.location.href = "/login";
  }
}
