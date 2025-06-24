package ru.intf.sasha.dto.gigachat;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GigaChatRequestDTO {
    private String model = "GigaChat";
    private List<GigaChatMessageDTO> messages;

    public GigaChatRequestDTO(List<GigaChatMessageDTO> messages) {
        this.messages = messages;
    }
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public List<GigaChatMessageDTO> getMessages() {
        return messages;
    }
    public void setMessages(List<GigaChatMessageDTO> messages) {
        this.messages = messages;
    }
   // Геттеры и сеттеры
}
