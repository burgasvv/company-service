package org.burgas.companyspringboot.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.operation.OperationFullResponse;
import org.burgas.companyspringboot.dto.operation.OperationRequest;
import org.burgas.companyspringboot.dto.operation.OperationShortResponse;
import org.burgas.companyspringboot.entity.operation.Operation;
import org.burgas.companyspringboot.entity.operation.OperationType;
import org.burgas.companyspringboot.entity.wallet.Wallet;
import org.burgas.companyspringboot.message.OperationMessages;
import org.burgas.companyspringboot.repository.OperationRepository;
import org.burgas.companyspringboot.repository.WalletRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class OperationMapper implements EntityMapper<OperationRequest, Operation, OperationShortResponse, OperationFullResponse> {

    private final OperationRepository operationRepository;
    private final ObjectFactory<WalletRepository> walletRepositoryObjectFactory;
    private final ObjectFactory<WalletMapper> walletMapperObjectFactory;

    private WalletRepository getWalletRepository() {
        return this.walletRepositoryObjectFactory.getObject();
    }

    private WalletMapper getWalletMapper() {
        return this.walletMapperObjectFactory.getObject();
    }

    @Override
    public Operation toEntity(OperationRequest operationRequest) {
        UUID operationId = this.handleData(operationRequest.getId(), UUID.randomUUID());
        return this.operationRepository.findById(operationId)
                .map(
                        operation -> {
                            OperationType operationType = this.handleData(operationRequest.getOperationType(),
                                    operation.getOperationType());
                            Double amount = this.handleData(operationRequest.getAmount(), operation.getAmount());
                            UUID senderWalletId = this.handleData(operationRequest.getSenderWalletId(), UUID.randomUUID());
                            Wallet senderWallet = this.handleData(
                                    this.getWalletRepository().findById(senderWalletId).orElse(null),
                                    operation.getSenderWallet()
                            );
                            UUID receiverWalletId = this.handleData(operationRequest.getReceiverWalletId(), UUID.randomUUID());
                            Wallet receiverWallet = this.handleData(
                                    this.getWalletRepository().findById(receiverWalletId).orElse(null),
                                    operation.getReceiverWallet()
                            );
                            return Operation.builder()
                                    .id(operation.getId())
                                    .operationType(operationType)
                                    .amount(amount)
                                    .senderWallet(senderWallet)
                                    .receiverWallet(receiverWallet)
                                    .createdAt(operation.getCreatedAt())
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            OperationType operationType = this.handleDataThrowable(operationRequest.getOperationType(),
                                    OperationMessages.OPERATION_TYPE_FIELD_EMPTY.getMessage()
                            );
                            Double amount = this.handleDataThrowable(operationRequest.getAmount(),
                                    OperationMessages.AMOUNT_FIELD_EMPTY.getMessage()
                            );
                            UUID senderWalletId = this.handleData(operationRequest.getSenderWalletId(), UUID.randomUUID());
                            Wallet senderWallet = this.handleDataThrowable(
                                    this.getWalletRepository().findById(senderWalletId).orElse(null),
                                    OperationMessages.SENDER_WALLET_FIELD_EMPTY.getMessage()
                            );
                            UUID receiverWalletId = this.handleData(operationRequest.getReceiverWalletId(), UUID.randomUUID());
                            Wallet receiverWallet = this.handleDataThrowable(
                                    this.getWalletRepository().findById(receiverWalletId).orElse(null),
                                    OperationMessages.RECEIVER_WALLET_FIELD_EMPTY.getMessage()
                            );
                            return Operation.builder()
                                    .operationType(operationType)
                                    .amount(amount)
                                    .senderWallet(senderWallet)
                                    .receiverWallet(receiverWallet)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                        }
                );
    }

    @Override
    public OperationShortResponse toShortResponse(Operation operation) {
        return OperationShortResponse.builder()
                .id(operation.getId())
                .operationType(operation.getOperationType())
                .amount(operation.getAmount())
                .createdAt(operation.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm:ss")))
                .build();
    }

    @Override
    public OperationFullResponse toFullResponse(Operation operation) {
        return OperationFullResponse.builder()
                .id(operation.getId())
                .operationType(operation.getOperationType())
                .amount(operation.getAmount())
                .senderWallet(
                        Optional.ofNullable(operation.getSenderWallet())
                                .map(wallet -> this.getWalletMapper().toShortResponse(wallet))
                                .orElse(null)
                )
                .receiverWallet(
                        Optional.ofNullable(operation.getReceiverWallet())
                                .map(wallet -> this.getWalletMapper().toShortResponse(wallet))
                                .orElse(null)
                )
                .createdAt(operation.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm:ss")))
                .build();
    }
}
