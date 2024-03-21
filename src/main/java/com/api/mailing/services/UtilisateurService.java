package com.api.mailing.services;

import com.api.mailing.dto.UtilisateurDto;
import com.api.mailing.entities.Utilisateur;
import com.api.mailing.exceptions.NotFoundException;
import com.api.mailing.repositories.UtilisateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UtilisateurService {

    private UtilisateurRepo utilisateurRepo;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurService(UtilisateurRepo utilisateurRepo) {
        this.utilisateurRepo = utilisateurRepo;
    }

//    public UtilisateurDto addUser(Utilisateur utilisateur){
//        if (utilisateur == null){
//            throw new NotFoundException("Veuillez remplir toutes les informations");
//        }
//        if (!EmailValidator.isValidEmail(utilisateur.getEmail())){
//            throw new RuntimeException("L'adresse mail n'est pas valide");
//        }
////        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
//        utilisateur.setPassword(utilisateur.getPassword());
//        Utilisateur user = utilisateurRepo.save(utilisateur);
//        UtilisateurDto utilisateurDto = new UtilisateurDto();
//        utilisateurDto.setId(utilisateur.getId());
//        utilisateurDto.setNom(utilisateur.getUsername());
//        utilisateurDto.setEmail(utilisateur.getEmail());
//        utilisateurDto.setActive(user.getActive());
//        return utilisateurDto;
//    }

    public UtilisateurDto getById(Long id){
        Utilisateur utilisateur = utilisateurRepo.findById(id).orElse(null);
        if (utilisateur == null){
            throw new NotFoundException("Aucun utilisateur avec l'id : " + id + " n'a ete trouve");
        }
        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(utilisateur.getId());
        utilisateurDto.setNom(utilisateur.getUsername());
        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setActive(utilisateur.getActive());
        return utilisateurDto;
    }

    public List<UtilisateurDto> getAll(){
        List<UtilisateurDto> utilisateurDtoList = new ArrayList<>();
        for (Utilisateur utilisateur : utilisateurRepo.findAll()){
            UtilisateurDto utilisateurDto = new UtilisateurDto();
            utilisateurDto.setId(utilisateur.getId());
            utilisateurDto.setNom(utilisateur.getUsername());
            utilisateurDto.setEmail(utilisateur.getEmail());
            utilisateurDto.setActive(utilisateur.getActive());
            utilisateurDtoList.add(utilisateurDto);
        }
        return utilisateurDtoList;
    }

    public UtilisateurDto editUser(Long id, Utilisateur utilisateur){
        Utilisateur user = utilisateurRepo.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("Aucun utilisateur avec l'id : " + id + "n'a ete trouve");
        }
        user.setEmail(utilisateur.getEmail());
        user.setUsername(utilisateur.getUsername());
        utilisateurRepo.save(user);
        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(user.getId());
        utilisateurDto.setNom(user.getUsername());
        utilisateurDto.setEmail(user.getEmail());
        utilisateurDto.setActive(user.getActive());
        return utilisateurDto;
    }

    public UtilisateurDto getByEmail(String email){
        Utilisateur utilisateur =  utilisateurRepo.findByEmail(email).orElse(null);
        if (utilisateur == null){
            throw new NotFoundException("Aucun utilisateur avec l'email : " + email + "n'a ete trouve");
        }

        UtilisateurDto utilisateurDto = new UtilisateurDto();
        utilisateurDto.setId(utilisateur.getId());
        utilisateurDto.setNom(utilisateur.getUsername());
        utilisateurDto.setEmail(utilisateur.getEmail());
        utilisateurDto.setActive(utilisateur.getActive());
        return utilisateurDto;
    }

//    Modifier un mot de passe
    public String changePassword(Long id, String oldPassword, String newPassword) throws Exception {
        Utilisateur utilisateur = utilisateurRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        if (!passwordEncoder.matches(oldPassword, utilisateur.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe fourni est incorrect.");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être le même que l'ancien.");
        }
        if (newPassword.length() < 8 || !containsLetterAndDigit(newPassword)) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit avoir au moins 8 caractères et contenir à la fois des lettres et des chiffres.");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        utilisateur.setPassword(encodedPassword);
        utilisateurRepo.save(utilisateur);
        throw new Exception("Modification effectuee avec succes");
    }

    private boolean containsLetterAndDigit(String str) {
        boolean containsLetter = false;
        boolean containsDigit = false;
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                containsLetter = true;
            } else if (Character.isDigit(c)) {
                containsDigit = true;
            }
            if (containsLetter && containsDigit) {
                return true;
            }
        }
        return false;
    }

    public String deleteUser(Long id){
        if (!utilisateurRepo.existsById(id)) {
            throw new NotFoundException("Aucun utilisateur avec l'id : " + id + "n'a ete trouve");
        }
        Utilisateur utilisateur = utilisateurRepo.findById(id).orElse(null);
        utilisateur.setActive(false);
        utilisateurRepo.save(utilisateur);
        throw new RuntimeException("Utilisateur desactiver");
    }
}
