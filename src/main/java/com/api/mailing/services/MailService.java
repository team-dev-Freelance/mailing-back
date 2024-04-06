package com.api.mailing.services;

import com.api.mailing.dto.Encrypted;
import com.api.mailing.dto.MailDto;
import com.api.mailing.entities.Mail;
import com.api.mailing.entities.STATUT;
import com.api.mailing.entities.Utilisateur;
import com.api.mailing.exceptions.NotFoundException;
import com.api.mailing.repositories.MailRepo;
import com.api.mailing.repositories.UtilisateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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

    public MailDto sendMail(Mail mail) throws Exception {
        Utilisateur utilisateur = utilisateurRepo.findByEmail(mail.getEmailExpediteur()).orElse(null);
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
            mail.setStatut(STATUT.envoyer);
        }

        mailRepo.save(mail);
        MailDto mailDto = new MailDto();
        mailDto.setId(mail.getId());
        mailDto.setEmailExpediteur(utilisateur.getEmail());
        mailDto.setObject(mail.getObjet());
        mailDto.setContent(mail.getContent());
        mailDto.setDate(mail.getDate());
        mailDto.setStatut(mail.getStatut());
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
                mail.setStatut(STATUT.recu);
                MailDto mailDto = new MailDto();
                mailDto.setId(mail.getId());
                mailDto.setObject(mail.getObjet());
                mailDto.setContent(decryptedMessage);
                mailDto.setDate(mail.getDate());
                mailDto.setEmailExpediteur(mail.getEmailExpediteur());
                mailDto.setStatut(mail.getStatut());
                mailDto.setUrlsJointPieces(mail.getUrlJointPieces());
                mailDtoList.add(mailDto);
                mailRepo.save(mail);
            }
        }
        return mailDtoList;
    }
/**
 *
 * BOITE D'ENVOI
 *
 *
 * ***/
    public List<MailDto> boiteEnvoi(Long id) throws Exception {
        Utilisateur utilisateur = utilisateurRepo.findById(id).orElse(null);
    //        String password = "koire@0312";
        if (utilisateur == null){
            throw new NotFoundException("Aucun compte avec l'id : " + id + "n'a ete trouve");
        }
        List<MailDto> mailDtoList = new ArrayList<>();
        for (Mail mail : mailRepo.findAll()){
           // Utilisateur user = utilisateurRepo.findByEmail(mail.getUtilisateur().getEmail()).orElse(null);
            if (mail.getEmailExpediteur().equals(utilisateur.getEmail())){
                String decryptedMessage = "";

                PrivateKey privateKey = KeyEncryption.decryptPrivateKey(mail.getPrivateKey());
                PublicKey publicKey = KeyEncryption.decryptPublicKey(mail.getPublicKey());
                boolean isValid = MailSignature.verifySignature(mail.getContent(), mail.getSignature(), publicKey);
                if (!isValid){
                    ChiffrementService chiffrementService = new ChiffrementService();
                    decryptedMessage = chiffrementService.decryptContent(mail.getContent(), mail.getSecretKey(), privateKey);
                }
                else {
                    throw new Exception("Mail corrompu");
                }
                mail.setStatut(STATUT.envoyer);
                MailDto mailDto = new MailDto();
                mailDto.setId(mail.getId());
                mailDto.setObject(mail.getObjet());
                mailDto.setContent(decryptedMessage);
                mailDto.setDate(mail.getDate());
                mailDto.setEmailExpediteur(mail.getEmailExpediteur());
                mailDto.setStatut(mail.getStatut());
                mailDto.setUrlsJointPieces(mail.getUrlJointPieces());
                mailDtoList.add(mailDto);
                mailRepo.save(mail);
            }
        }
        return mailDtoList;
    }
    /*************************************************************************************************/
    public List<MailDto> getListMailByStatut(Long id, STATUT statut) throws Exception {
        Utilisateur utilisateur = utilisateurRepo.findById(id).orElse(null);
        if (utilisateur == null){
            throw new NotFoundException("Aucun utilisateur pour l'id: "+id);
        }
        if (mailRepo.findAll().isEmpty()){
            throw new NotFoundException("Aucun enregistrement dans la base de donnees");
        }
        if (mailRepo.findAll().isEmpty()){
            throw new NotFoundException("Aucun enregistrement dans la base de donnees");
        }
        List<MailDto> mailDtoList = new ArrayList<>();
        for (Mail mail: mailRepo.findAll()){
            Utilisateur user = utilisateurRepo.findById(mail.getUtilisateur().getId()).orElse(null);
            if (user.getEmail().equals(utilisateur.getEmail()) && mail.getStatut().equals(statut)){
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
                mailDto.setStatut(mail.getStatut());
                mailDto.setUrlsJointPieces(mail.getUrlJointPieces());
                mailDtoList.add(mailDto);
            }
        }
        if (mailDtoList.isEmpty()){
            throw new NotFoundException("Aucun enregistrement dans la base de donnees");
        }
        return mailDtoList;
    }

    public MailDto editStatutMail(Long id, STATUT statut) throws Exception {
        Mail mail = mailRepo.findById(id).orElse(null);
        if (mail == null){
            throw new NotFoundException("Aucun mail avec l'id: "+id+" dans la base de donnee");
        }
        mail.setStatut(statut);
        mailRepo.save(mail);
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
        mailDto.setStatut(statut);
        mailDto.setUrlsJointPieces(mail.getUrlJointPieces());

        return mailDto;
    }

    public MailDto editMail(Long id, Mail mail) throws Exception {
        Mail mai = mailRepo.findById(id).orElse(null);
        if (mai == null){
            throw new NotFoundException("Aucun mail pour l'id: "+id);
        }
        String decryptedMessage = "";
        PrivateKey privateKey = KeyEncryption.decryptPrivateKey(mai.getPrivateKey());
        PublicKey publicKey = KeyEncryption.decryptPublicKey(mai.getPublicKey());
        boolean isValid = MailSignature.verifySignature(mai.getContent(), mai.getSignature(), publicKey);
        if (!isValid){
            ChiffrementService chiffrementService = new ChiffrementService();
            decryptedMessage = chiffrementService.decryptContent(mai.getContent(), mai.getSecretKey(), privateKey);
        }else {
            throw new Exception("Mail corrompu");
        }
        mai.setUrlJointPieces(mail.getUrlJointPieces());
        mai.setSignature(mail.getSignature());
        mai.setContent(mail.getContent());
        mai.setDate(mail.getDate());
        mai.setSecretKey(mail.getSecretKey());
        mai.setPublicKey(mail.getPublicKey());
        mai.setPrivateKey(mail.getPrivateKey());
        mai.setEmailExpediteur(mail.getEmailExpediteur());
        mai.setStatut(mail.getStatut());
        mai.setObjet(mail.getObjet());
        mai.setUtilisateur(mai.getUtilisateur());
        mailRepo.save(mai);
        MailDto mailDto = new MailDto();
        mailDto.setId(mail.getId());
        mailDto.setObject(mail.getObjet());
        mailDto.setContent(decryptedMessage);
        mailDto.setDate(mail.getDate());
        mailDto.setEmailExpediteur(mail.getEmailExpediteur());
        mailDto.setStatut(mail.getStatut());
        mailDto.setUrlsJointPieces(mail.getUrlJointPieces());

        return mailDto;
    }

    public String deleteMail(Long id) {
        if (!mailRepo.existsById(id)) {
            throw new NotFoundException("Aucun mail avec l'id : " + id + "n'a ete trouve");
        }
        mailRepo.deleteById(id);
        return "Mail supprimer";
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
        return "Suppresion reussi";
    }
}
