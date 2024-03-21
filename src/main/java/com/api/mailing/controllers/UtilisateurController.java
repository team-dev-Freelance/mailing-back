package com.api.mailing.controllers;

import com.api.mailing.dto.Password;
import com.api.mailing.dto.UtilisateurDto;
import com.api.mailing.entities.Utilisateur;
import com.api.mailing.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/admin/user")
public class UtilisateurController {

    private UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

//    @PostMapping("/add")
//    public ResponseEntity<UtilisateurDto> saveUser(@RequestBody Utilisateur utilisateur){
//        return new ResponseEntity<>(utilisateurService.addUser(utilisateur), HttpStatus.OK);
//    }

    @GetMapping("/findAll")
    public ResponseEntity<List<UtilisateurDto>> getAllUsers(){
        return new ResponseEntity<>(utilisateurService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<UtilisateurDto> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(utilisateurService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/findByEmail")
    public ResponseEntity<UtilisateurDto> getUserByEmail(@RequestParam("email") String email){
        return new ResponseEntity<>(utilisateurService.getByEmail(email), HttpStatus.OK);
    }

    @GetMapping("/user/changePassword/{id}")
    public ResponseEntity<String> changedPassword(@PathVariable Long id, @RequestBody Password password) throws Exception {
        return new ResponseEntity<>(utilisateurService.changePassword(id, password.getOldPassword(), password.getNewPassword()), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UtilisateurDto> updateUserById(@PathVariable Long id, @RequestBody Utilisateur utilisateur){
        return new ResponseEntity<>(utilisateurService.editUser(id, utilisateur), HttpStatus.OK);
    }

    @PutMapping("/deleteUserById/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id){
        return new ResponseEntity<>(utilisateurService.deleteUser(id), HttpStatus.OK);
    }
}
