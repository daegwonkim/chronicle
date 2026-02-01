package io.github.daegwonkim.chronicle.dto.admins;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignUpDto {
    @Schema(description = "회원가입 요청 DTO")
    public record Req(
            @Schema(description = "아이디", example = "admin")
            String username,

            @Schema(description = "비밀번호", example = "admin")
            String password
    ) {}
}
