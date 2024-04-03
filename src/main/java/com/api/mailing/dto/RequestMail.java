package com.api.mailing.dto;

import com.api.mailing.entities.STATUT;
import com.api.mailing.entities.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMail {

    private String objet;
    private String content;
    private String emailExpediteur;
    private MultipartFile file;
    private Long userId;
}
