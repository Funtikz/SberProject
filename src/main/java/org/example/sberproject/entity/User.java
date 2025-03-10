package org.example.sberproject.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserImage> userImages;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceDeal> userServices;


}
