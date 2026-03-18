package com.blinkergate.user;

import com.blinkergate.auth.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int XP_PER_BLINKER = 10;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserDto.Response getProfile(String username) {
        User user = getUser(username);
        return UserDto.Response.from(user);
    }

    @Transactional
    public UserDto.Response updateProfile(String username, UserDto.UpdateRequest req) {
        User user = getUser(username);
        boolean usernameChanged = false;

        String newUsername = req.getUsername();
        if (!newUsername.equals(username)) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("Username already taken");
            }
            user.setUsername(newUsername);
            usernameChanged = true;
        }

        if (req.getRole() != null && !req.getRole().isBlank()) {
            user.setRole(req.getRole());
        }

        if (req.getCurrentXp() != null) {
            user.setCurrentXp(req.getCurrentXp());
            recalculateLevel(user);
        }

        User saved = userRepository.save(user);
        UserDto.Response response = UserDto.Response.from(saved);

        // Issue a fresh token so the frontend's JWT stays in sync with the new username.
        // Without this, all subsequent requests would authenticate as the old username.
        if (usernameChanged) {
            response.setToken(jwtUtil.generateToken(saved.getUsername()));
        }

        return response;
    }

    @Transactional
    public UserDto.Response awardBlinkerXp(String username, int totalXp) {
        User user = getUser(username);
        user.setCurrentXp(user.getCurrentXp() + totalXp);
        recalculateLevel(user);
        return UserDto.Response.from(userRepository.save(user));
    }

    private void recalculateLevel(User user) {
        int xpThreshold = user.getLvl() * 100;
        while (user.getCurrentXp() >= xpThreshold) {
            user.setCurrentXp(user.getCurrentXp() - xpThreshold);
            user.setLvl(user.getLvl() + 1);
            xpThreshold = user.getLvl() * 100;
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}