package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.operation.OperationFullResponse;
import org.burgas.companyspringboot.dto.operation.OperationRequest;
import org.burgas.companyspringboot.entity.operation.Operation;
import org.burgas.companyspringboot.exception.OperationNotFoundException;
import org.burgas.companyspringboot.mapper.OperationMapper;
import org.burgas.companyspringboot.message.OperationMessages;
import org.burgas.companyspringboot.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;

    public Operation findEntity(final UUID operationId) {
        return this.operationRepository.findById(operationId == null ? UUID.randomUUID() : operationId)
                .orElseThrow(() -> new OperationNotFoundException(OperationMessages.OPERATION_NOT_FOUND.getMessage()));
    }

    public OperationFullResponse findById(final UUID operationId) {
        return this.operationMapper.toFullResponse(this.findEntity(operationId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(final OperationRequest operationRequest) {
        Operation operation = this.operationMapper.toEntity(operationRequest);
        return this.operationRepository.save(operation).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(final UUID operationId) {
        Operation operation = this.findEntity(operationId);
        this.operationRepository.delete(operation);
    }
}
