package com.api.mailing.entities;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.api.mailing.entities.Permission.ADMIN_CREATE;
import static com.api.mailing.entities.Permission.ADMIN_DELETE;
import static com.api.mailing.entities.Permission.ADMIN_READ;
import static com.api.mailing.entities.Permission.ADMIN_UPDATE;
import static com.api.mailing.entities.Permission.USER_CREATE;
import static com.api.mailing.entities.Permission.USER_DELETE;
import static com.api.mailing.entities.Permission.USER_READ;
import static com.api.mailing.entities.Permission.USER_UPDATE;

@RequiredArgsConstructor
public enum Role {

  DEFAULT(Collections.emptySet()),
  ADMIN(
          Set.of(
                  ADMIN_READ,
                  ADMIN_UPDATE,
                  ADMIN_DELETE,
                  ADMIN_CREATE
          )
  ),
  USER(
          Set.of(
                  USER_READ,
                  USER_UPDATE,
                  USER_DELETE,
                  USER_CREATE
          )
  )

  ;

  @Getter
  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }
}


