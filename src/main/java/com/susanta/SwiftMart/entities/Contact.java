package com.susanta.SwiftMart.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "Contact")
@Table(name = "contacts")
public class Contact {
    @Id
    private String contactId;

    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePic;

    @Column(length = 1000)
    private String description;
    private boolean favourite = false;
    private String website;
    private String facebookLink;
    // private List<SocialLink> socialLinks = new ArrayList<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<SocialLink> socialLinks = new ArrayList<>();
}
