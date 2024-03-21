package com.api.mailing.controllers;

import com.api.mailing.dto.MailDto;
import com.api.mailing.entities.Mail;
import com.api.mailing.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/user/mail")
public class MailController {

    private MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<MailDto> sendMail(@PathVariable Long id, @RequestBody Mail mail) throws Exception {
        return new ResponseEntity<>(mailService.sendMail(id, mail), HttpStatus.OK);
    }

    @GetMapping("/boite/{id}")
    public ResponseEntity<List<MailDto>> getEmailListByUser(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.boiteDeReception(id), HttpStatus.OK);
    }

    @DeleteMapping("/deleteOneMail/{id}")
    public ResponseEntity<String> deleteOneMail(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.deleteMail(id), HttpStatus.OK);
    }

//    @DeleteMapping("/mail/deleteMailSelect")
//    public ResponseEntity<String> deleteMailSelect(@RequestBody List<Long> idList){
//        return new ResponseEntity<>(mailService.deleteAllById(idList), HttpStatus.OK);
//    }

    @PutMapping("/deleteBoiteUser/{id}")
    public ResponseEntity<String> deleteBoiteUser(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(mailService.deleteAllMailUser(id), HttpStatus.OK);
    }
}
