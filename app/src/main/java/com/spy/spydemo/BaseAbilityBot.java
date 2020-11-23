package com.spy.spydemo;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

public class BaseAbilityBot extends DefaultAbsSender {

    protected BaseAbilityBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return null;
    }
}
