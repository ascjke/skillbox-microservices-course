package ru.borisov.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;
import ru.borisov.users.controller.request.UpdateUserInfoRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
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

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    @JsonIgnore
    private String passwordHash;

    private String lastName;
    private String firstName;
    private String middleName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

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

    @OneToMany(mappedBy = "to",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Subscription> followers;

    @OneToMany(mappedBy = "from",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Subscription> following;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public int getFollowersCount() {
        return followers.size();
    }

    @Transient
    public int getFollowingCount() {
        return following.size();
    }

    public boolean isInfoUpdated(UpdateUserInfoRequest request) {
        if (!Objects.equals(request.getLastName(), this.lastName)) {
            return true;
        }
        if (!Objects.equals(request.getFirstName(), this.firstName)) {
            return true;
        }
        if (!Objects.equals(request.getMiddleName(), this.middleName)) {
            return true;
        }
        if (!Objects.equals(request.getGender(), this.gender)) {
            return true;
        }
        if (!Objects.equals(request.getBirthDate(), this.birthDate)) {
            return true;
        }
        if (!Objects.equals(request.getCity(), this.city)) {
            return true;
        }
        if (!Objects.equals(request.getProfileImage(), this.profileImage)) {
            return true;
        }
        if (!Objects.equals(request.getBio(), this.bio)) {
            return true;
        }

        // Если все поля совпадают, возвращаем false
        return false;
    }
}
