package com.app.two.factor.auth.user;

import com.app.two.factor.auth.jwt.JwtToken;
import com.app.two.factor.auth.jwt.JwtUtils;
import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.utils.FacialRecognizer;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Erro ao encontrar usuário com o id="+id)
        );
    }

    @Transactional
    public JwtToken save(UserCreateDTO userCreateDTO) {
        UserEntity user = new UserEntity(userCreateDTO.getName(), userCreateDTO.getEmail(),
                userCreateDTO.getPassword(), -1);

        userRepository.save(user);

        return JwtUtils.createToken(user.getEmail(), "ROLE_STEP1_COMPLETED");
    }

    @Transactional
    public JwtToken saveLabelFacialRecognition(MultipartFile fileFace, long userId) throws IOException {
        FacialRecognizer facialRecognizer = new FacialRecognizer();

        int label = facialRecognizer.recognizeFace(fileFace);
        UserEntity user = this.findById(userId);

        user.setLabelFacialRecognition(label);

        userRepository.save(user);

        return JwtUtils.createToken(user.getEmail(), "ROLE_USER");
    }
}
