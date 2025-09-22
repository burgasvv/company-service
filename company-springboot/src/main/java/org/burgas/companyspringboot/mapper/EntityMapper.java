package org.burgas.companyspringboot.mapper;

import org.burgas.companyspringboot.dto.Request;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.burgas.companyspringboot.exception.EntityFieldEmptyException;
import org.springframework.stereotype.Component;

@Component
public interface EntityMapper<R extends Request, E extends BaseEntity, S extends Response, F extends Response> {

    default <D> D handleData(final D requestData, final D entityData) {
        return requestData == null || requestData == "" ? entityData : requestData;
    }

    default <D> D handleDataThrowable(final D requestData, final String message) {
        if (requestData == null || requestData == "")
            throw new EntityFieldEmptyException(message);
        return requestData;
    }

    E toEntity(R r);

    S toShortResponse(E e);

    F toFullResponse(E e);
}
