package com.example.resource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate birthday;
    @Column(name = "user_type")
    private String userType;

    @Column(name = "join_date", updatable = false)
    @Builder.Default
    private LocalDate joinDate = LocalDate.now();
    @Column
    @Builder.Default
    private String role = "ROLE_USER";

    @OneToMany(mappedBy = "member")
    private List<OrigImage> origImages;
}