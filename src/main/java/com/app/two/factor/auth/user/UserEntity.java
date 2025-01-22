package com.app.two.factor.auth.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "Usuarios")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String password;

    @Column(name = "label_reconhecimento_facial")
    private int labelFacialRecognition;

    public UserEntity(String name, String email, String password, int label) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.setLabelFacialRecognition(label);
    }
}
