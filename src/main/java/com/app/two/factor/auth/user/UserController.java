package com.app.two.factor.auth.user;

import com.app.two.factor.auth.jwt.JwtToken;
import com.app.two.factor.auth.jwt.JwtUserDetails;
import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
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

    @PostMapping("/verify-face")
    @PreAuthorize("hasRole('STEP1_COMPLETED')")
    public ResponseEntity<JwtToken> verifyFace(MultipartFile fileFace,
                                         @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        JwtToken token = userService.saveLabelFacialRecognition(fileFace, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserResponseDTO(user.getId(), user.getName(), user.getName()));
    }
}
