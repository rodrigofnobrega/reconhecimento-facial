package com.app.two.factor.auth.user;

import com.app.two.factor.auth.jwt.JwtToken;
import com.app.two.factor.auth.jwt.JwtUserDetails;
import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.user.dto.UserLoginDTO;
import com.app.two.factor.auth.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<JwtToken> createUser(@RequestBody @Valid UserCreateDTO createDTO) {
        JwtToken token = userService.save(createDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/register-face")
    @PreAuthorize("hasRole('ROLE_STEP1_SIGNUP_COMPLETED')")
    public ResponseEntity<Void> registerFace(MultipartFile frontFace, MultipartFile leftFace, MultipartFile rightFace,
                                         @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        userService.registerFace(frontFace, leftFace, rightFace, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserResponseDTO(user.getId(), user.getName(), user.getName()));
    }

    @PostMapping("/verify-face")
    @PreAuthorize("hasRole('ROLE_STEP1_SIGIN_COMPLETED')")
    public ResponseEntity<JwtToken> verifyFace(MultipartFile frontFace,
                                           @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        JwtToken token = userService.verifyFace(frontFace, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        JwtToken token = userService.login(userLoginDTO);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
