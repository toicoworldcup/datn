package org.example.doantn.Controller;

import org.example.doantn.Dto.response.UserInfoResponse;
import org.example.doantn.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint trả về thông tin người dùng
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(Principal principal) {
        String username = principal.getName();  // Lấy tên người dùng từ principal
        UserInfoResponse userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(userInfo);
    }
}
