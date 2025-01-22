package com.app.two.factor.auth.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    @Email(message = "formato do e-mail invalido")
    private String email;
    @NotBlank
    @Size(min = 5)
    private String password;
}
