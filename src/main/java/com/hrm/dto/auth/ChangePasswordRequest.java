package com.hrm.dto.auth;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.Size; import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor public class ChangePasswordRequest { @NotBlank private String currentPassword; @NotBlank @Size(min=6, message="Mật khẩu mới phải có ít nhất 6 ký tự.") private String newPassword; }
