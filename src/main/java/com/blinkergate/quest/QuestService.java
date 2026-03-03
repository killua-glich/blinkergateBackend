package com.blinkergate.quest;

import com.blinkergate.user.User;
import com.blinkergate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final UserRepository userRepository;

    public List<QuestDto.Response> getQuestsForUser(String username) {
        User user = getUser(username);
        return questRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(QuestDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestDto.Response createQuest(String username, QuestDto.CreateRequest req) {
        User user = getUser(username);
        Quest quest = new Quest();
        quest.setUser(user);
        quest.setTitle(req.getTitle());
        quest.setDescription(req.getDescription());
        quest.setCategory(req.getCategory());
        quest.setRepeatType(req.getRepeatType());
        return QuestDto.Response.from(questRepository.save(quest));
    }

    @Transactional
    public QuestDto.Response toggleComplete(String username, Long questId) {
        Quest quest = getOwnedQuest(username, questId);
        boolean currentlyCompleted = quest.isEffectivelyCompleted();

        if (currentlyCompleted) {
            // Undo completion
            quest.setIsCompleted(false);
            quest.setLastCompleted(null);
        } else {
            quest.setIsCompleted(true);
            quest.setLastCompleted(LocalDate.now());
        }
        return QuestDto.Response.from(questRepository.save(quest));
    }

    @Transactional
    public void deleteQuest(String username, Long questId) {
        Quest quest = getOwnedQuest(username, questId);
        questRepository.delete(quest);
    }

    private Quest getOwnedQuest(String username, Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new RuntimeException("Quest not found"));
        if (!quest.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }
        return quest;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
