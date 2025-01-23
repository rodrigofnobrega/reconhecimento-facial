package com.app.two.factor.auth.infra.jwt;

import com.app.two.factor.auth.exception.EntityNotFoundException;
import com.app.two.factor.auth.user.UserEntity;
import com.app.two.factor.auth.user.UserRepository;
import com.app.two.factor.auth.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado.")
        );


        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String email) {
        return JwtUtils.createToken(email, "ROLE_USUARIO");
    }
}