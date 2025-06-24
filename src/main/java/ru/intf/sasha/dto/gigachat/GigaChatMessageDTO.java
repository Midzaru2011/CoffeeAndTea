package ru.intf.sasha.dto.gigachat;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GigaChatMessageDTO {
    private String role;
    private String content;

    // Пустой конструктор нужен для Jackson
    public GigaChatMessageDTO() {}

    public GigaChatMessageDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Геттеры обязательны!
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}