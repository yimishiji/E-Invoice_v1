package com.rongzer.efapiao.service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
    private String username;
    private String password;

    public MailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    String getPassword() {
        return this.password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }

    String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}