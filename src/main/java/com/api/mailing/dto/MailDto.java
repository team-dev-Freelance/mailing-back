package com.api.mailing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {

    private Long id;
    private String emailExpediteur;
    private String object;
    private String content;
    private Boolean openMail;
    private List<String> urlsJointPieces;
    private Date date;
}
