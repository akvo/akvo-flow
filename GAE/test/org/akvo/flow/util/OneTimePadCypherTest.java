package org.akvo.flow.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OneTimePadCypherTest {
    
    final String secretKey = "very-secret-key";
    final String encriptedValue = "AgAKDQ";
    final String valueToEncrypt = "text";

    @Test
    void encrypt() {
        assertEquals(OneTimePadCypher.encrypt(secretKey, valueToEncrypt), encriptedValue);
    }

    @Test
    void decrypt() {
        assertEquals(OneTimePadCypher.decrypt(secretKey, encriptedValue), valueToEncrypt);
    }
}