package com.blinkergate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// XP needed to reach the next level. Formula: level * 100
// e.g. level 1 -> level 2 requires 100 XP, level 2 -> level 3 requires 200 XP, etc.

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int XP_PER_BLINKER = 10;

    private final UserRepository userRepository;

    public UserDto.Response getProfile(String username) {
        User user = getUser(username);
        return UserDto.Response.from(user);
    }

    @Transactional
    public UserDto.Response updateProfile(String username, UserDto.UpdateRequest req) {
        User user = getUser(username);

        // Update username if changed
        String newUsername = req.getUsername();
        if (!newUsername.equals(username)) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("Username already taken");
            }
            user.setUsername(newUsername);
        }

        // Update role if provided
        if (req.getRole() != null && !req.getRole().isBlank()) {
            user.setRole(req.getRole());
        }

        // Update XP if provided (direct set, e.g. from quest reward)
        if (req.getCurrentXp() != null) {
            user.setCurrentXp(req.getCurrentXp());
            recalculateLevel(user);
        }

        return UserDto.Response.from(userRepository.save(user));
    }

    /**
     * Called on every quest completion ("blinker"). Awards XP and levels up if threshold met.
     * This prevents XP spam because XP only increments here — quest completion is the gate.
     */
    @Transactional
    public UserDto.Response awardBlinkerXp(String username) {
        User user = getUser(username);
        user.setCurrentXp(user.getCurrentXp() + XP_PER_BLINKER);
        recalculateLevel(user);
        return UserDto.Response.from(userRepository.save(user));
    }

    /**
     * Checks whether the user has enough XP to level up and does so repeatedly
     * until they no longer meet the threshold. XP carries over.
     */
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
