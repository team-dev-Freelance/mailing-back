package com.api.mailing.services;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyEncryption {

    // Méthode pour chiffrer une clé privée avec AES
    public static byte[] encryptPrivateKey(PrivateKey privateKey) throws Exception {

        byte[] encryptedPrivateKeyBytes = privateKey.getEncoded();

        // Convertir les octets chiffrés en une représentation Base64 pour stockage sécurisé
//        return Base64.getEncoder().encode(encryptedPrivateKeyBytes);
        return encryptedPrivateKeyBytes;
    }

    // Méthode pour déchiffrer une clé privée chiffrée avec AES
    public static PrivateKey decryptPrivateKey(byte[] encryptedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // ou l'algorithme correspondant à votre clé privée

        // Créez une spécification de clé privée à partir des octets de la clé privée
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encryptedPrivateKey);

        // Générer la clé privée à partir de la spécification de clé privée
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    // Méthode pour chiffrer une clé publique avec AES
    public static byte[] encryptPublicKey(PublicKey publicKey) {

        byte[] encryptedPublicKeyBytes = publicKey.getEncoded();

        return encryptedPublicKeyBytes;
    }

    // Méthode pour déchiffrer une clé publique chiffrée avec AES
    public static PublicKey decryptPublicKey(byte[] encryptedPublicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // ou l'algorithme correspondant à votre clé publique

        // Créez une spécification de clé publique à partir des octets de la clé publique
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encryptedPublicKey);

        // Générer la clé publique à partir de la spécification de clé publique
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return publicKey;
    }

}
