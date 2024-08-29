package com.example.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateData {
    private org.telegram.telegrambots.meta.api.objects.Message message;

    public boolean hasMessage() {
        return true;
    }

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private Long chatId;
        private From from;

        @Setter
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class From {
            private String firstName;
        }
    }
}
