package com.api.mailing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDto {

    private Long id;
    private String nom;
    private String email;
    private Boolean active;
}
