package com.app.two.factor.auth.user;

import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid UserCreateDTO createDTO) {
        userService.save(createDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> sendFace(MultipartFile fileFace, @PathVariable Long id) throws IOException {
        userService.saveLabelFacialRecognition(fileFace, id);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserResponseDTO(user.getId(), user.getName(), user.getName()));
    }
}
