package com.victor.springjwtauthentication.services;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        return Pattern.compile("^(.+)@(\\S+)$").matcher(s).matches();
    }
}
