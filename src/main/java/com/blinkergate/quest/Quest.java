package com.blinkergate.quest;

import com.blinkergate.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quests")
@Data
@NoArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "quest_category")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private QuestCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", columnDefinition = "repeat_type")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private RepeatType repeatType;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "last_completed")
    private LocalDate lastCompleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isEffectivelyCompleted() {
        if (!isCompleted) return false;
        if (repeatType == RepeatType.DAILY) {
            return lastCompleted != null && lastCompleted.equals(LocalDate.now());
        }
        return true;
    }
}
