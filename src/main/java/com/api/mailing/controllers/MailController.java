package com.api.mailing.controllers;

import com.api.mailing.dto.MailDto;
import com.api.mailing.dto.RequestMail;
import com.api.mailing.entities.Mail;
import com.api.mailing.entities.STATUT;
import com.api.mailing.repositories.UtilisateurRepo;
import com.api.mailing.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/user/mail")
public class MailController {

    private MailService mailService;
    private UtilisateurRepo utilisateurRepo;

    @Autowired
    public MailController(MailService mailService, UtilisateurRepo utilisateurRepo) {
        this.mailService = mailService;
        this.utilisateurRepo = utilisateurRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<MailDto> sendMail(@ModelAttribute RequestMail request) throws Exception {

        Mail mail = new Mail();
        if (!request.getFile().isEmpty()){
            String dir = System.getProperty("user.dir");
            String url = dir+"/src/main/resources/assets/"+request.getFile().getOriginalFilename();
            mail.setUrlJointPieces(url);
            File convertFile = new File(url);
            convertFile.createNewFile();
            try(FileOutputStream out = new FileOutputStream(convertFile)){
                out.write(request.getFile().getBytes());
            }catch (Exception exe){
                exe.printStackTrace();
            }
        }
        mail.setContent(request.getContent());
        mail.setObjet(request.getObjet());
        mail.setEmailExpediteur(request.getEmailExpediteur());
        mail.setUtilisateur(utilisateurRepo.findById(request.getUserId()).orElse(null));

        return new ResponseEntity<>(mailService.sendMail(mail), HttpStatus.OK);
    }

//    @PostMapping("/add/{id}")
//    public ResponseEntity<MailDto> sendMail(@PathVariable Long id, @RequestBody Mail mail) throws Exception {
//        return new ResponseEntity<>(mailService.sendMail(id, mail), HttpStatus.OK);
//    }

    @GetMapping("/boite/{id}")
    public ResponseEntity<List<MailDto>> getEmailListByUser(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.boiteDeReception(id), HttpStatus.OK);
    }
    /*************************************************************************************************/
    @GetMapping("/boiteenvoi/{id}")
    public ResponseEntity<List<MailDto>> boiteReception(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.boiteEnvoi(id), HttpStatus.OK);
    }

    @GetMapping("/boitelu/{id}")
    public ResponseEntity<List<MailDto>> boitelu(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.boiteEnvoi(id), HttpStatus.OK);
    }
    /*************************************************************************************************/
    @GetMapping("/findByStatut/{id}/statut")
    public ResponseEntity<List<MailDto>> getEmailListByStatut(@PathVariable Long id, @RequestParam STATUT statut) throws Exception {
        return new ResponseEntity<>(mailService.getListMailByStatut(id, statut), HttpStatus.OK);
    }

    @DeleteMapping("/deleteOneMail/{id}")
    public ResponseEntity<String> deleteOneMail(@PathVariable Long id) {
        return new ResponseEntity<>(mailService.deleteMail(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MailDto> updateMail(@PathVariable Long id, @RequestBody Mail mail) throws Exception {
        return new ResponseEntity<>(mailService.editMail(id, mail), HttpStatus.OK);
    }

    @GetMapping("/updateStatut/{id}/statut")
    public ResponseEntity<MailDto> updateStatutMail(@PathVariable Long id, @RequestParam STATUT statut) throws Exception {
        return new ResponseEntity<>(mailService.editStatutMail(id, statut), HttpStatus.OK);
    }

//    @DeleteMapping("/mail/deleteMailSelect")
//    public ResponseEntity<String> deleteMailSelect(@RequestBody List<Long> idList){
//        return new ResponseEntity<>(mailService.deleteAllById(idList), HttpStatus.OK);
//    }

    @DeleteMapping("/deleteBoiteUser/{id}")
    public ResponseEntity<String> deleteBoiteUser(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.deleteAllMailUser(id), HttpStatus.OK);
    }
}
