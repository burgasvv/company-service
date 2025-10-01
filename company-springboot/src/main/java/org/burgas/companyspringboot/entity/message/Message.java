package org.burgas.companyspringboot.entity.message;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.identity.Identity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "message", schema = "public")
@NamedEntityGraph(
        name = "message-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "sender"),
                @NamedAttributeNode(value = "chat", subgraph = "chat-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "chat-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "identities")
                        }
                )
        }
)
public final class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Identity sender;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
