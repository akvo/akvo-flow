package org.akvo.flow.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base64Test {
    @Test
    public void usingBytesBothImplementationShouldReturnTheSameEncodedString() {

        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);

        assertEquals(Base64.getEncoder().encodeToString(bytes), encodeBase64String(bytes));
    }

    @Test
    public void usingStringBothImplementationsShouldReturnTheSameEncodedString() {
        String value = "Lorem ipsum dolor sit amet";
        assertEquals(Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)),
                encodeBase64String(value.getBytes(StandardCharsets.UTF_8)));
    }
}
