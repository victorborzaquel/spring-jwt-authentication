package com.victor.springjwtauthentication.interfaces;

public interface EmailSender {
    void send(String to, String email);
}
