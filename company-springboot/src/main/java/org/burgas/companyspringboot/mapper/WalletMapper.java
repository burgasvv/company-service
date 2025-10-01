package org.burgas.companyspringboot.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.wallet.WalletFullResponse;
import org.burgas.companyspringboot.dto.wallet.WalletRequest;
import org.burgas.companyspringboot.dto.wallet.WalletShortResponse;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.wallet.Wallet;
import org.burgas.companyspringboot.message.WalletMessages;
import org.burgas.companyspringboot.repository.IdentityRepository;
import org.burgas.companyspringboot.repository.WalletRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class WalletMapper implements EntityMapper<WalletRequest, Wallet, WalletShortResponse, WalletFullResponse> {

    private final WalletRepository walletRepository;
    private final ObjectFactory<IdentityRepository> identityRepositoryObjectFactory;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<OperationMapper> operationMapperObjectFactory;

    private IdentityRepository getIdentityRepository() {
        return this.identityRepositoryObjectFactory.getObject();
    }

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private OperationMapper getOperationMapper() {
        return this.operationMapperObjectFactory.getObject();
    }

    @Override
    public Wallet toEntity(WalletRequest walletRequest) {
        UUID walletId = this.handleData(walletRequest.getId(), UUID.randomUUID());
        return this.walletRepository.findById(walletId)
                .map(
                        wallet -> {
                            UUID identityId = this.handleData(walletRequest.getIdentityId(), UUID.randomUUID());
                            Identity identity = this.handleData(
                                    this.getIdentityRepository().findById(identityId).orElse(null),
                                    wallet.getIdentity()
                            );
                            return Wallet.builder()
                                    .id(wallet.getId())
                                    .balance(wallet.getBalance())
                                    .identity(identity)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            UUID identityId = this.handleData(walletRequest.getIdentityId(), UUID.randomUUID());
                            Identity identity = this.handleDataThrowable(
                                    this.getIdentityRepository().findById(identityId).orElse(null),
                                    WalletMessages.IDENTITY_ENTITY_FIELD_EMPTY.getMessage()
                            );
                            return Wallet.builder()
                                    .balance(0.0)
                                    .identity(identity)
                                    .build();
                        }
                );
    }

    @Override
    public WalletShortResponse toShortResponse(Wallet wallet) {
        return WalletShortResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .identity(
                        Optional.ofNullable(wallet.getIdentity())
                                .map(identity -> this.getIdentityMapper()
                                        .toShortResponse(identity))
                                .orElse(null)
                )
                .build();
    }

    @Override
    public WalletFullResponse toFullResponse(Wallet wallet) {
        return WalletFullResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .identity(
                        Optional.ofNullable(wallet.getIdentity())
                                .map(identity -> this.getIdentityMapper()
                                        .toShortResponse(identity))
                                .orElse(null)
                )
                .senderOperations(
                        wallet.getSenderOperations()
                                .stream()
                                .map(operation -> this.getOperationMapper()
                                        .toShortResponse(operation))
                                .toList()
                )
                .receiverOperations(
                        wallet.getReceiverOperations()
                                .stream()
                                .map(operation -> this.getOperationMapper()
                                        .toShortResponse(operation))
                                .toList()
                )
                .build();
    }
}
