package org.burgas.companyspringboot.repository;

import org.burgas.companyspringboot.entity.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, UUID> {

    @Override
    @EntityGraph(value = "identity-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Identity> findById(@NotNull UUID uuid);

    Optional<Identity> findIdentityByUsername(String username);
}
