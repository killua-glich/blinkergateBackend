package com.blinkergate.quest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuestDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private QuestCategory category;
        @NotNull
        private RepeatType repeatType;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private QuestCategory category;
        private RepeatType repeatType;
        private boolean completed;
        private LocalDate lastCompleted;
        private LocalDateTime createdAt;

        public static Response from(Quest quest) {
            Response r = new Response();
            r.id = quest.getId();
            r.title = quest.getTitle();
            r.description = quest.getDescription();
            r.category = quest.getCategory();
            r.repeatType = quest.getRepeatType();
            r.completed = quest.isEffectivelyCompleted();
            r.lastCompleted = quest.getLastCompleted();
            r.createdAt = quest.getCreatedAt();
            return r;
        }
    }
}
