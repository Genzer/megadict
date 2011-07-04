package com.megadict.model.parser;

public interface TextParser<RETURN_TYPE> {
    RETURN_TYPE parse(String text);
}
