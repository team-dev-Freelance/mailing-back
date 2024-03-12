package com.api.mailing.services;

import java.security.*;

public class GeneratorKey {

    private static final String RSA_ALGORITHM = "RSA";
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(2048); // Taille de la clé RSA en bits
        return keyPairGenerator.generateKeyPair();
    }
}
