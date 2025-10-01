package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.operation.OperationRequest;
import org.burgas.companyspringboot.dto.wallet.WalletFullResponse;
import org.burgas.companyspringboot.dto.wallet.WalletRequest;
import org.burgas.companyspringboot.dto.wallet.WalletShortResponse;
import org.burgas.companyspringboot.entity.operation.OperationType;
import org.burgas.companyspringboot.entity.wallet.Wallet;
import org.burgas.companyspringboot.exception.NotEnoughMoneyException;
import org.burgas.companyspringboot.exception.WalletNotFoundException;
import org.burgas.companyspringboot.exception.WrongAmountException;
import org.burgas.companyspringboot.mapper.WalletMapper;
import org.burgas.companyspringboot.message.WalletMessages;
import org.burgas.companyspringboot.repository.WalletRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final ObjectFactory<IdentityService> identityServiceObjectFactory;
    private final ObjectFactory<OperationService> operationServiceObjectFactory;

    private IdentityService getIdentityService() {
        return this.identityServiceObjectFactory.getObject();
    }

    private OperationService getOperationService() {
        return this.operationServiceObjectFactory.getObject();
    }

    private Wallet findEntityWithPessimisticWriteLock(final UUID walletId) {
        return this.walletRepository.findWalletById(walletId == null ? UUID.randomUUID() : walletId)
                .orElseThrow(() -> new WalletNotFoundException(WalletMessages.WALLET_NOT_FOUND.getMessage()));
    }

    public Wallet findEntity(final UUID walletId) {
        return this.walletRepository.findById(walletId == null ? UUID.randomUUID() : walletId)
                .orElseThrow(() -> new WalletNotFoundException(WalletMessages.WALLET_NOT_FOUND.getMessage()));
    }

    public List<WalletShortResponse> findByIdentity(final UUID identityId) {
        return this.getIdentityService().findEntity(identityId)
                .getWallets()
                .stream()
                .map(this.walletMapper::toShortResponse)
                .toList();
    }

    public WalletFullResponse findById(final UUID walletId) {
        return this.walletMapper.toFullResponse(this.findEntity(walletId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(final WalletRequest walletRequest) {
        Wallet wallet = this.walletMapper.toEntity(walletRequest);
        return this.walletRepository.save(wallet).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(final UUID walletId) {
        Wallet wallet = this.findEntity(walletId);
        this.walletRepository.delete(wallet);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID deposit(final UUID walletId, final Double amount) {
        Wallet wallet = this.findEntityWithPessimisticWriteLock(walletId);
        if (amount > 0)
            wallet.setBalance(wallet.getBalance() + amount);
        else
            throw new WrongAmountException(WalletMessages.WRONG_AMOUNT.getMessage());

        OperationRequest operationRequest = OperationRequest.builder()
                .operationType(OperationType.DEPOSIT)
                .amount(amount)
                .senderWalletId(wallet.getId())
                .receiverWalletId(wallet.getId())
                .build();
        return this.getOperationService().createOrUpdate(operationRequest);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID withdraw(final UUID walletId, final Double amount) {
        Wallet wallet = this.findEntityWithPessimisticWriteLock(walletId);
        if (wallet.getBalance() < amount)
            throw new NotEnoughMoneyException(WalletMessages.NOT_ENOUGH_MONEY.getMessage());

        if (amount > 0)
            wallet.setBalance(wallet.getBalance() - amount);
        else
            throw new WrongAmountException(WalletMessages.WRONG_AMOUNT.getMessage());

        OperationRequest operationRequest = OperationRequest.builder()
                .operationType(OperationType.WITHDRAW)
                .amount(amount)
                .senderWalletId(wallet.getId())
                .receiverWalletId(wallet.getId())
                .build();
        return this.getOperationService().createOrUpdate(operationRequest);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID transfer(final UUID senderWalletId, final UUID receiverWalletId, final Double amount) {
        Wallet senderWallet = this.findEntityWithPessimisticWriteLock(senderWalletId);
        Wallet receiverWallet = this.findEntityWithPessimisticWriteLock(receiverWalletId);
        if (senderWallet.getBalance() < amount)
            throw new NotEnoughMoneyException(WalletMessages.NOT_ENOUGH_MONEY.getMessage());

        if (amount > 0) {
            senderWallet.setBalance(senderWallet.getBalance() - amount);
            receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        } else {
            throw new WrongAmountException(WalletMessages.WRONG_AMOUNT.getMessage());
        }

        OperationRequest operationRequest = OperationRequest.builder()
                .operationType(OperationType.TRANSFER)
                .amount(amount)
                .senderWalletId(senderWallet.getId())
                .receiverWalletId(receiverWallet.getId())
                .build();
        return this.getOperationService().createOrUpdate(operationRequest);
    }
}
