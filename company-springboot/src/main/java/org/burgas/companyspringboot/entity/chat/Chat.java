package org.burgas.companyspringboot.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.message.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "chat", schema = "public")
@NamedEntityGraph(
        name = "chat-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "identities"),
                @NamedAttributeNode(value = "messages"),
        }
)
public final class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @ManyToMany(mappedBy = "chats", fetch = FetchType.LAZY)
    private List<Identity> identities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();
}
