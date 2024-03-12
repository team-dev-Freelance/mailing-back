package com.api.mailing.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MAIL")
public class Mail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String objet;

    @Column(length = 4096)
    private String content;

    @Column
    private Boolean openMail = false;

    @Column
    private String emailExpediteur;

    @Column
    private Date date = new Date();

    @Column(length = 4096)
    private byte[] signature;

    @Column(length = 4096)
    private byte[] privateKey;

    @Column(length = 4096)
    private byte[] publicKey;

    @Column(length = 4096)
    private byte[] secretKey;

    @Column
    private List<String> urlJointPieces;

    @ManyToOne
    @JoinColumn(name = "UTILISATEUR_ID")
    private Utilisateur utilisateur;
}
