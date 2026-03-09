package com.blinkergate.auth;

import com.blinkergate.user.User;
import com.blinkergate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public com.blinkergate.auth.AuthDto.TokenResponse register(com.blinkergate.auth.AuthDto.RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return new com.blinkergate.auth.AuthDto.TokenResponse(jwtUtil.generateToken(user.getUsername()),
                user.getUsername(), user.getLvl(), user.getRole(), user.getCurrentXp());
    }

    public com.blinkergate.auth.AuthDto.TokenResponse login(com.blinkergate.auth.AuthDto.LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new AuthDto.TokenResponse(jwtUtil.generateToken(user.getUsername()),
                user.getUsername(), user.getLvl(), user.getRole(), user.getCurrentXp());
    }
}
