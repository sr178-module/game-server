package com.sr178.game.server.http.util;

import java.nio.charset.Charset;

public class StringEncoder {
    public static final String UTF_8_NAME = "UTF-8";

    public static final Charset UTF_8 = Charset.forName(UTF_8_NAME);

    public static byte[] encode(String toDo){
        return toDo.getBytes(UTF_8);
    }

    public static String encode(byte[] b){
        return new String(b, UTF_8);
    }
    
    public static String encode(byte[] b, int offset, int len){
        return new String(b, offset, len, UTF_8);
    }
}
