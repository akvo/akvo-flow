/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */
package org.akvo.flow.util;

import java.util.Base64;

public class OneTimePadCypher {

    public static String encrypt(final String secretKey, final String text) {
      return new String(Base64.getEncoder().encode(xor(secretKey, text.getBytes())));
    }

    public static String decrypt(final String secretKey, final String hash) {
      try {
        return new String(xor(secretKey, Base64.getDecoder().decode(hash.getBytes())), "UTF-8");
      } catch (java.io.UnsupportedEncodingException ex) {
        throw new IllegalStateException(ex);
      }
    }
    private static byte[] xor(final String secretKey, final byte[] input) {
      final byte[] output = new byte[input.length];
      final byte[] secret = secretKey.getBytes();
      int spos = 0;
      for (int pos = 0; pos < input.length; ++pos) {
        output[pos] = (byte) (input[pos] ^ secret[spos]);
        spos += 1;
        if (spos >= secret.length) {
          spos = 0;
        }
      }
      return output;
    }
  
}
