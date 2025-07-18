package org.example.requesters.skeleton.interfaces;

import org.example.models.BaseModel;

public interface CrudEndpointInterface  {
    Object post(BaseModel requestBody);
    Object get(Long id);
    Object update(Long id, BaseModel requestBody);
    Object delete(Long id);
}
