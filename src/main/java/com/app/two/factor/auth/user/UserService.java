package com.app.two.factor.auth.user;

import com.app.two.factor.auth.exception.BadCredentialsException;
import com.app.two.factor.auth.exception.EntityNotFoundException;
import com.app.two.factor.auth.exception.RecognizingFaceException;
import com.app.two.factor.auth.infra.jwt.JwtToken;
import com.app.two.factor.auth.infra.jwt.JwtUtils;
import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.user.dto.UserLoginDTO;
import com.app.two.factor.auth.utils.FacialRecognizer;
import com.app.two.factor.auth.utils.FacialRecognizerTraining;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Erro ao encontrar usuário com o id="+id)
        );
    }

    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Erro ao encontrar usuário com o email="+email)
        );
    }

    @Transactional
    public JwtToken save(UserCreateDTO userCreateDTO) {
        UserEntity user = new UserEntity(userCreateDTO.getName(), userCreateDTO.getEmail(),
                userCreateDTO.getPassword(), -1);

        userRepository.save(user);

        return JwtUtils.createToken(user.getEmail(), "ROLE_STEP1_SIGNUP_COMPLETED");
    }

    @Transactional
    public Void save(UserEntity user) {
        userRepository.save(user);

        return null;
    }

    @Transactional
    public Void registerFace(MultipartFile fileFace, MultipartFile leftFace,
                                           MultipartFile rightFace, long userId) throws IOException {
        // Encontra o usuário pelo ID
        UserEntity user = this.findById(userId);

        // Nome do diretório será o nome do usuário (substituindo vírgulas por underscores para evitar problemas)
        String sanitizedUsername = user.getName().replace(" ", "_").toLowerCase();
        Path userDirectory = Paths.get("src/main/resources/assets/database/" + sanitizedUsername);

        // Cria o diretório se não existir
        if (!Files.exists(userDirectory)) {
            Files.createDirectories(userDirectory);
        }

        // Caminho completo para salvar a foto no diretório
        Path photoPath1 = userDirectory.resolve(fileFace.getOriginalFilename());
        Path photoPath2 = userDirectory.resolve(leftFace.getOriginalFilename());
        Path photoPath3 = userDirectory.resolve(rightFace.getOriginalFilename());

        // Salva o arquivo no diretório
        Files.write(photoPath1, fileFace.getBytes());
        Files.write(photoPath2, leftFace.getBytes());
        Files.write(photoPath3, rightFace.getBytes());

        while (!Files.exists(photoPath1) || !Files.exists(photoPath2) || !Files.exists(photoPath3)) { }

        System.out.println("Escreveu no disco");

        // Usa o reconhecimento facial para gerar o rótulo (label)
        FacialRecognizerTraining facialRecognizerTraining = new FacialRecognizerTraining();
        int userLabel = facialRecognizerTraining.trainingModel(sanitizedUsername);

        user.setLabelFacialRecognition(userLabel);

        this.save(user);

        return null;
    }

    @Transactional(readOnly = true)
    public JwtToken verifyFace(MultipartFile fileFace, Long userId) throws IOException {
        UserEntity user = this.findById(userId);

        if (this.verifyFaceUtils(fileFace) == user.getLabelFacialRecognition()) {
            return JwtUtils.createToken(user.getEmail(), "ROLE_USER");
        }

        throw new RecognizingFaceException("Erro ao reconhecer face");
    }

    @Transactional(readOnly = true)
    public JwtToken login(UserLoginDTO userLoginDTO) {
        UserEntity user = this.findByEmail(userLoginDTO.getEmail());

        if (userLoginDTO.getPassword().equals(user.getPassword())) {
            return JwtUtils.createToken(user.getEmail(), "ROLE_STEP1_SIGIN_COMPLETED");
        }

        throw new BadCredentialsException("Credenciais inválidas ao realizar login");
    }

    private int verifyFaceUtils(MultipartFile fileFace) throws IOException {
        FacialRecognizer facialRecognizer = new FacialRecognizer();
        return facialRecognizer.recognizeFace(fileFace);
    }

}
