package com.jwt.project.model;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User implements UserDetails{

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private int id;
     @Column(name = "first_name")
     private String firstName;
     @Column(name = "last_name")
     private String lastName;
//     @Column(unique = true)
     private String username;
     private String password;
//     This means the information about this (user) who has the (token) is stored in the (token) class itself.
     @OneToMany(mappedBy = "user")
     @JsonManagedReference
     private List<Token> tokens;

     @Enumerated(value = EnumType.STRING)
     private Role role;
//     private String[] roles;

     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
//          List<GrantedAuthority> authorities = Arrays.stream(roles)
//                  .map(role -> new SimpleGrantedAuthority(role))
//                  .collect(Collectors.toList());
         return List.of(new SimpleGrantedAuthority(role.name()));
     }

     @Override
     public boolean isAccountNonExpired() {
          return true;
     }

     @Override
     public boolean isAccountNonLocked() {
          return true;
     }

     @Override
     public boolean isCredentialsNonExpired() {
          return true;
     }

     @Override
     public boolean isEnabled() {
          return true;
     }
}
