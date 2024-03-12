package com.api.mailing.services;

import com.api.mailing.dto.Encrypted;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class ChiffrementService {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 128;

    public Encrypted encryptContent(String content, PublicKey publicKey) throws Exception {
        // Génère une clé AES pour le chiffrement symétrique
        SecretKey secretKey = generateAESKey();

        // Chiffre le contenu du mail avec la clé AES
        byte[] encryptedContent = encryptAES(content.getBytes(), secretKey);

        // Chiffre la clé AES avec la clé publique RSA
        byte[] encryptedKey = encryptRSA(secretKey.getEncoded(), publicKey);
        Encrypted encrypted = new Encrypted();
        encrypted.setSecretKey(encryptedKey);
        // Concatène la clé AES chiffrée et le contenu chiffré
        byte[] encryptedData = new byte[encryptedKey.length + encryptedContent.length];
        System.arraycopy(encryptedKey, 0, encryptedData, 0, encryptedKey.length);
        System.arraycopy(encryptedContent, 0, encryptedData, encryptedKey.length, encryptedContent.length);
        encrypted.setData(Base64.getEncoder().encodeToString(encryptedData));
        return encrypted;
    }

    public String decryptContent(String encryptedContent, byte[] encryptedKey, PrivateKey privateKey) throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedContent);

//        // Récupère la clé AES chiffrée et le contenu chiffré
//        int keySize = AES_KEY_SIZE / 8;
//        byte[] encryptedKey = new byte[keySize];
        int keySize = encryptedKey.length;
        byte[] content = new byte[encryptedData.length - keySize];
        System.arraycopy(encryptedData, 0, encryptedKey, 0, keySize);
        System.arraycopy(encryptedData, keySize, content, 0, content.length);

        // Déchiffre la clé AES avec la clé privée RSA
        byte[] decryptedKey = decryptRSA(encryptedKey, privateKey);

        // Déchiffre le contenu du mail avec la clé AES
        byte[] decryptedContent = decryptAES(content, new SecretKeySpec(decryptedKey, AES_ALGORITHM));

        return new String(decryptedContent);
    }

    // Chiffre un tableau d'octets avec AES
    private byte[] encryptAES(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // Déchiffre un tableau d'octets avec AES
    private byte[] decryptAES(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    // Chiffre un tableau d'octets avec RSA
    private byte[] encryptRSA(byte[] data, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // Déchiffre un tableau d'octets avec RSA
    private byte[] decryptRSA(byte[] encryptedData, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    // Génère une clé AES
    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }
}
