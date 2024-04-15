package com.jwt.project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "token")
    private String token;
    @Column(name = "is_logged_out")
    private boolean isLoggedOut;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    // This is a Foreign key, and it will store the ID of the user who is associated with this token
//    @JsonIgnore
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;
}
