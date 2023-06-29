package ru.borisov.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "_user")
@SQLDelete(sql = "UPDATE users_scheme._user SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(unique = true)
    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String passwordHash;

    private String lastName;
    private String firstName;
    private String middleName;

    @Enumerated(EnumType.STRING)
    private Male male;

    private LocalDate birthDate;
    private String city;
    private String profileImage;
    private String bio;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @BatchSize(size = 10)
    @JoinTable(
            name = "user_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    private String phone;

    private boolean deleted;

    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"user"})
    private Set<Follower> followers = new HashSet<>();

    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"user"})
    private Set<Following> followings = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
