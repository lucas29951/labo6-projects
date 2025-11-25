package com.labdevs.comandar.data.dto;

public class RegisterRequest {
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    public RegisterRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
