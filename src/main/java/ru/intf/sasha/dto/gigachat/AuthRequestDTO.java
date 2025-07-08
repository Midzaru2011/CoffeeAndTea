package ru.intf.sasha.dto.gigachat;

public class AuthRequestDTO {
    private String grant_type = "client_credentials";

    public String getGrant_type() {
        return grant_type;
    }
}