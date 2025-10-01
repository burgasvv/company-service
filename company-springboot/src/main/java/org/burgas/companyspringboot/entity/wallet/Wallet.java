package org.burgas.companyspringboot.entity.wallet;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.operation.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "wallet", schema = "public")
@NamedEntityGraph(
        name = "wallet-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "identity"),
                @NamedAttributeNode(value = "senderOperations"),
                @NamedAttributeNode(value = "receiverOperations")
        }
)
public final class Wallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id", referencedColumnName = "id")
    private Identity identity;

    @Builder.Default
    @OneToMany(mappedBy = "senderWallet", fetch = FetchType.LAZY)
    private List<Operation> senderOperations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiverWallet", fetch = FetchType.LAZY)
    private List<Operation> receiverOperations = new ArrayList<>();
}
