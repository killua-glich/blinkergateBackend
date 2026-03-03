package com.blinkergate.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByUserIdOrderByCreatedAtDesc(Long userId);
}
