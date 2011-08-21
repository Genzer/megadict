package com.megadict.format.dict.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.megadict.exception.OperationFailedException;

public class UnicodeDecoder {

    private UnicodeDecoder() {
    }
    
    public static CharBuffer decode(ByteBuffer input) {
        try {
            CharBuffer decoded = DECODER.decode(input);
            DECODER.reset();
            return decoded;
        } catch (CharacterCodingException cce) {
            throw new OperationFailedException("decoding a ByteBuffer input", cce);
        }
    }

    private static final Charset UNICODE_CHARSET = Charset.forName("UTF-8");
    private static final CharsetDecoder DECODER = UNICODE_CHARSET.newDecoder();
}
