package ru.borisov.users.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"from_user_fk", "to_user_fk"})
)
public class Follower {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "from_user_fk")
    private User from;

    @ManyToOne
    @JoinColumn(name = "to_user_fk")
    private User to;
}
