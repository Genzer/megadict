package com.megadict.model;

import java.util.Locale;

public interface Speaker {
    void speak(String text);
    Locale getSupportedLanguage();
}
