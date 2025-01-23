package com.app.two.factor.auth.user;

import com.app.two.factor.auth.infra.RestErrorMessage;
import com.app.two.factor.auth.infra.jwt.JwtToken;
import com.app.two.factor.auth.infra.jwt.JwtUserDetails;
import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.user.dto.UserLoginDTO;
import com.app.two.factor.auth.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Usuários", description = "Contém todas as operações relativas a autenticação e login de usuário")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Cadastrar um usuário",
            description = "Cadastrará um novo usuário no sistema. Retornará um token para ser usado na etapa de cadastro facial",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                    @ApiResponse(responseCode = "422", description = "Campo(s) inválido(s)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @PostMapping
    public ResponseEntity<JwtToken> createUser(@RequestBody @Valid UserCreateDTO createDTO) {
        JwtToken token = userService.save(createDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @Operation(summary = "Cadastrar face",
            description = "Cadastrará a face no sistema.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso.")
            })
    @PostMapping("/register-face")
    @PreAuthorize("hasRole('ROLE_STEP1_SIGNUP_COMPLETED')")
    public ResponseEntity<Void> registerFace(MultipartFile frontFace, MultipartFile leftFace, MultipartFile rightFace,
                                         @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        userService.registerFace(frontFace, leftFace, rightFace, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Buscar usuário pelo ID",
            description = "Irá buscar o usuário pelo ID. Endpoint para testar se o login foi correto.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário cadastrado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não foi encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserResponseDTO(user.getId(), user.getName(), user.getName()));
    }

    @Operation(summary = "Verificar face.",
            description = "Irá verificar se a face corresponde com o usuário para retornar o token JWT final. Última etapa de login",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Face verificada com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                    @ApiResponse(responseCode = "500", description = "Erro ao reconhecer face.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
            })
    @PostMapping("/verify-face")
    @PreAuthorize("hasRole('ROLE_STEP1_SIGIN_COMPLETED')")
    public ResponseEntity<JwtToken> verifyFace(MultipartFile frontFace,
                                           @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        JwtToken token = userService.verifyFace(frontFace, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @Operation(summary = "Realizar login",
            description = "Irá realizar login com email e senha. Retornará token para a última etapa do login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Credenciais verificadas com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                    @ApiResponse(responseCode = "400", description = "Credenciais inválidas.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
            })
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        JwtToken token = userService.login(userLoginDTO);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
