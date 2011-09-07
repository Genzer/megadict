package com.megadict.format.dict.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import com.megadict.exception.OperationFailedException;

public class UnicodeDecoder {

    private static final Charset UNICODE_CHARSET = Charset.forName("UTF-8");
    private static final CharsetDecoder DECODER;

    static {
        DECODER = UNICODE_CHARSET.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    private UnicodeDecoder() {
    }

    public synchronized static CharBuffer decode(ByteBuffer input) {
        try {
            CharBuffer decoded = DECODER.decode(input);
            DECODER.reset();
            return decoded;
        } catch (CharacterCodingException cce) {
            throw new OperationFailedException("decoding a ByteBuffer input", cce);
        }
    }

    public synchronized static void decode(ByteBuffer in, CharBuffer out, boolean endOfInput) {
        DECODER.decode(in, out, true);
        DECODER.flush(out);
        DECODER.reset();
    }
}
