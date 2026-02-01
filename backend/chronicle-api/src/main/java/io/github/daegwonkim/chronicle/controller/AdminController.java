package io.github.daegwonkim.chronicle.controller;

import io.github.daegwonkim.chronicle.dto.admins.SignInDto;
import io.github.daegwonkim.chronicle.dto.admins.SignUpDto;
import io.github.daegwonkim.chronicle.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "회원가입", description = "새로운 계정을 생성합니다.")
    @PostMapping
    public void signUp(@RequestBody SignUpDto.Req req) {
        adminService.signUp(req);
    }

    @Operation(summary = "로그인", description = "기존 계정으로 로그인합니다.")
    @PostMapping
    public void signIn(@RequestBody SignInDto.Req req) {
        adminService.signIn(req);
    }
}
