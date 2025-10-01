package org.burgas.companyspringboot.repository;

import org.burgas.companyspringboot.entity.message.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Override
    @EntityGraph(value = "message-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Message> findById(@NotNull UUID uuid);
}
