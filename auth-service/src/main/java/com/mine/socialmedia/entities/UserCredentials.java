package com.mine.socialmedia.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "auth_db",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"phone_number"})
})
@EntityListeners(AuditingEntityListener.class)
public class UserCredentials implements UserDetails {
    @Id
    @GeneratedValue()
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private UUID userId;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            updatable = false,
            length = 50
    )
    private String email;

    @Column(
            name = "phone_number",
            nullable = false,
            unique = true,
            updatable = false,
            length = 20
    )
    private String phoneNumber;

    @JsonIgnore
    @Column(
            name = "password",
            nullable = false,
            length = 100
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "user_role",
            nullable = false,
            columnDefinition = "VARCHAR(15) DEFAULT 'USER'"
    )
    private CredentialsRole userCredentialsRole;

    @Column(
            name = "user_enabled",
            nullable = false,
            columnDefinition = "VARCHAR(15) DEFAULT 'PENDING'"
    )
    @Enumerated(EnumType.STRING)
    private CredentialsEnabled accountEnabled;

    @Column(
            name = "user_status",
            nullable = false,
            columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'"
    )
    @Enumerated(EnumType.STRING)
    private UserCredentialsStatus userCredentialsStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "last_logged_in")
    private LocalDate lastLoggedIn;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(this.userCredentialsRole.name())
        );
    }

    @Override
    public String getUsername() {
        assert this.userId != null;
        return Optional.of(this.userId.toString()).orElse(this.userId.toString());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.lastLoggedIn == null ||
                LocalDate.now().isBefore(this.lastLoggedIn.plusDays(30));
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.userCredentialsStatus == UserCredentialsStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.updatedAt == null ||
                LocalDate.now().isBefore(this.updatedAt.plusDays(30));
    }

    @Override
    public boolean isEnabled() {
        return this.accountEnabled == CredentialsEnabled.ENABLED;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
