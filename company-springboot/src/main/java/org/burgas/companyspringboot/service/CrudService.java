package org.burgas.companyspringboot.service;

import org.burgas.companyspringboot.dto.Request;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.entity.BaseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CrudService<K, R extends Request, E extends BaseEntity, S extends Response, F extends Response> {

    E findEntity(final K id);

    List<S> findAll();

    F findById(final K id);

    K createOrUpdate(final R r);

    void delete(final K k);
}
