package com.gamegoo.gamegoo_v2.account.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordRequest {

    @Email(message = "Email 형식이 올바르지 않습니다.")
    @NotBlank(message = "Email은 비워둘 수 없습니다.")
    String email;

    @NotBlank(message = "newPassword는 비워둘 수 없습니다.")
    String newPassword;

    @NotBlank(message = "verifyCode는 비워둘 수 없습니다.")
    String verifyCode;

}
