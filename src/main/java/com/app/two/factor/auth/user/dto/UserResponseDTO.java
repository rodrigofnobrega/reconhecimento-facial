package com.app.two.factor.auth.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserResponseDTO {
    private long id;
    private String name;
    private String email;
}
