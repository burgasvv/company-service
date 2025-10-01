package org.burgas.companyspringboot.repository;

import org.burgas.companyspringboot.entity.chat.Chat;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Override
    @EntityGraph(value = "chat-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NotNull Optional<Chat> findById(@NotNull UUID uuid);
}
