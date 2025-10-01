package org.burgas.companyspringboot.repository;

import jakarta.persistence.LockModeType;
import org.burgas.companyspringboot.entity.wallet.Wallet;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Override
    @EntityGraph(value = "wallet-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Wallet> findById(@NotNull UUID uuid);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findWalletById(UUID id);
}
