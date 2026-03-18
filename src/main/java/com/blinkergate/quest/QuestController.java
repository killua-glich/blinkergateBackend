package com.blinkergate.quest;

import com.blinkergate.user.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @GetMapping
    public ResponseEntity<List<QuestDto.Response>> getQuests(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questService.getQuestsForUser(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<QuestDto.Response> createQuest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuestDto.CreateRequest request) {
        return ResponseEntity.ok(questService.createQuest(userDetails.getUsername(), request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<QuestDto.Response> toggleComplete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(questService.toggleComplete(userDetails.getUsername(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        questService.deleteQuest(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redeemBlinker")
    public ResponseEntity<UserDto.Response> redeemBlinker(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questService.redeemBlinker(userDetails.getUsername()));
    }
}
