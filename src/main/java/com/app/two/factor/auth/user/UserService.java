package com.app.two.factor.auth.user;

import com.app.two.factor.auth.user.dto.UserCreateDTO;
import com.app.two.factor.auth.utils.FacialRecognizer;
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
    public Void save(UserCreateDTO userCreateDTO) {
        UserEntity user = new UserEntity(userCreateDTO.getName(), userCreateDTO.getEmail(), userCreateDTO.getPassword());

        userRepository.save(user);
        return null;
    }

    @Transactional
    public void saveLabelFacialRecognition(MultipartFile fileFace, long userId) throws IOException {
        FacialRecognizer facialRecognizer = new FacialRecognizer();

        int label = facialRecognizer.recognizeFace(fileFace);
        UserEntity user = this.findById(userId);

        user.setLabelFacialRecognition(label);

        userRepository.save(user);
    }
}
