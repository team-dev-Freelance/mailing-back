package com.api.mailing.services;

import com.api.mailing.dto.Encrypted;
import com.api.mailing.dto.MailDto;
import com.api.mailing.entities.Mail;
import com.api.mailing.entities.Utilisateur;
import com.api.mailing.exceptions.NotFoundException;
import com.api.mailing.repositories.MailRepo;
import com.api.mailing.repositories.UtilisateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Service
public class MailService {

    private MailRepo mailRepo;
    private UtilisateurRepo utilisateurRepo;

    @Autowired
    public MailService(MailRepo mailRepo, UtilisateurRepo utilisateurRepo) {
        this.mailRepo = mailRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    public MailDto sendMail(Long id, Mail mail) throws Exception {
        Utilisateur utilisateur = utilisateurRepo.findById(id).orElse(null);
        if (utilisateur == null){
            throw new NotFoundException("L'expediteur du mail est inconnu");
        }else if (utilisateur.getActive().equals(false)){
            throw new NotFoundException("Utilisateur(expediteur) desactiver");
        }
        Utilisateur user = utilisateurRepo.findById(mail.getUtilisateur().getId()).orElse(null);
        if (user.getActive().equals(false)){
            throw new NotFoundException("Utilisateur(destinataire) du mail est inconnu");
        }
        KeyPair keyPair = GeneratorKey.generateKeyPair();
        if (keyPair != null){
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            ChiffrementService chiffrementService = new ChiffrementService();
            Encrypted encrypted = chiffrementService.encryptContent(mail.getContent(), publicKey);
            String encryptedMessage = encrypted.getData();

            byte[] signature = MailSignature.signMail(mail.getContent(), privateKey);

            mail.setSignature(signature);
            byte[] encryptPrivateKey = KeyEncryption.encryptPrivateKey(privateKey);
            byte[] encryptPublicKey = KeyEncryption.encryptPublicKey(publicKey);
            mail.setPublicKey(encryptPublicKey);
            mail.setPrivateKey(encryptPrivateKey);
            mail.setSecretKey(encrypted.getSecretKey());
            mail.setEmailExpediteur(utilisateur.getEmail());
            mail.setContent(encryptedMessage);
        }

        mailRepo.save(mail);
        MailDto mailDto = new MailDto();
        mailDto.setId(mail.getId());
        mailDto.setEmailExpediteur(utilisateur.getEmail());
        mailDto.setObject(mail.getObjet());
        mailDto.setContent(mail.getContent());
        mailDto.setDate(mail.getDate());
        mailDto.setOpenMail(mail.getOpenMail());
        mailDto.setUrlsJointPieces(mail.getUrlJointPieces());
        return mailDto;
    }

    public List<MailDto> boiteDeReception(Long id) throws Exception {
        Utilisateur utilisateur = utilisateurRepo.findById(id).orElse(null);
//        String password = "koire@0312";
        if (utilisateur == null){
            throw new NotFoundException("Aucun compte avec l'id : " + id + "n'a ete trouve");
        }
        List<MailDto> mailDtoList = new ArrayList<>();
        for (Mail mail : mailRepo.findAll()){
            Utilisateur user = utilisateurRepo.findById(mail.getUtilisateur().getId()).orElse(null);
            if (user.getEmail().equals(utilisateur.getEmail())){
                String decryptedMessage = "";

                PrivateKey privateKey = KeyEncryption.decryptPrivateKey(mail.getPrivateKey());
                PublicKey publicKey = KeyEncryption.decryptPublicKey(mail.getPublicKey());
                boolean isValid = MailSignature.verifySignature(mail.getContent(), mail.getSignature(), publicKey);
                if (!isValid){
                    ChiffrementService chiffrementService = new ChiffrementService();
                    decryptedMessage = chiffrementService.decryptContent(mail.getContent(), mail.getSecretKey(), privateKey);
                }else {
                    throw new Exception("Mail corrompu");
                }
                MailDto mailDto = new MailDto();
                mailDto.setId(mail.getId());
                mailDto.setObject(mail.getObjet());
                mailDto.setContent(decryptedMessage);
                mailDto.setDate(mail.getDate());
                mailDto.setEmailExpediteur(mail.getEmailExpediteur());
                mailDto.setOpenMail(mail.getOpenMail());
                mailDto.setUrlsJointPieces(mail.getUrlJointPieces());
                mailDtoList.add(mailDto);
            }
        }
        return mailDtoList;
    }

    public String deleteMail(Long id) throws Exception {
        if (!mailRepo.existsById(id)) {
            throw new NotFoundException("Aucun mail avec l'id : " + id + "n'a ete trouve");
        }
        mailRepo.deleteById(id);
        throw new Exception("Mail supprimer");
    }

    public String deleteAllMailUser(Long id) throws Exception {
        if (!utilisateurRepo.existsById(id)){
            throw new NotFoundException("Aucun utilisateur avec l'id : " + id + "n'a ete trouve");
        }
        List<MailDto> mailDtoList = boiteDeReception(id);
        List<Mail> mailList = new ArrayList<>();
        for (MailDto mailDto : mailDtoList){
            Mail mail = mailRepo.findById(mailDto.getId()).orElse(null);
            if (mail != null){
                mailList.add(mail);
            }
        }
        mailRepo.deleteAll(mailList);
        throw new RuntimeException("Suppresion reussi");
    }
}
