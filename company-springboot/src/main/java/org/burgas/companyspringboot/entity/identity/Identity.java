package org.burgas.companyspringboot.entity.identity;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.company.Company;
import org.burgas.companyspringboot.entity.wallet.Wallet;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "identity", schema = "public")
@NamedEntityGraph(
        name = "identity-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "company"),
                @NamedAttributeNode(value = "chats"),
                @NamedAttributeNode(value = "wallets")
        }
)
public final class Identity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private Authority authority;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "patronymic", nullable = false)
    private String patronymic;

    @Column(name = "about", nullable = false)
    private String about;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_chat",
            joinColumns = @JoinColumn(name = "identity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "id")
    )
    private List<Chat> chats = new ArrayList<>();

    public void addChat(final Chat chat) {
        this.chats.add(chat);
        chat.getIdentities().add(this);
    }

    @Builder.Default
    @OneToMany(mappedBy = "identity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return enabled || !UserDetails.super.isEnabled();
    }
}
