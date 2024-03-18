package com.api.mailing.services;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class MailSignature {

    // Méthode pour signer le contenu d'un mail avec la clé privée de l'expéditeur
    public static byte[] signMail(String content, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(content.getBytes());
        byte[] signatureBytes = signature.sign();
        return signatureBytes;
    }

    // Méthode pour vérifier la signature d'un mail avec la clé publique du destinataire
    public static boolean verifySignature(String content, byte[] signature, PublicKey publicKey) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(content.getBytes());
        return verifier.verify(signature);
    }

}

