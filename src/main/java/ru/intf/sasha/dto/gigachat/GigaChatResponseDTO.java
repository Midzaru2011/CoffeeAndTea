package ru.intf.sasha.dto.gigachat;

public class GigaChatResponseDTO {

    private Choice choice;

    public String getAnswer() {
        return choice != null && choice.message != null ? choice.message.content : "";
    }

    private static class Choice {
        private Message message;
    }

    private static class Message {
        private String content;
    }
}