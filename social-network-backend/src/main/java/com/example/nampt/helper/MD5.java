package com.example.nampt.helper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class MD5 {
    public static String encrypt(String m, String k) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = k.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(m.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public static String decrypt(String c, String k) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = k.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(c)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
