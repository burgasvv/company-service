package org.burgas.companyspringboot.entity.operation;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.burgas.companyspringboot.entity.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "operation", schema = "public")
@NamedEntityGraph(
        name = "operation-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "senderWallet", subgraph = "sender-subgraph"),
                @NamedAttributeNode(value = "receiverWallet", subgraph = "receiver-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "sender-subgraph",
                        attributeNodes = @NamedAttributeNode(value = "identity")
                ),
                @NamedSubgraph(
                        name = "receiver-subgraph",
                        attributeNodes = @NamedAttributeNode(value = "identity")
                )
        }
)
public final class Operation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_wallet_id", referencedColumnName = "id")
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_wallet_id", referencedColumnName = "id")
    private Wallet receiverWallet;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
