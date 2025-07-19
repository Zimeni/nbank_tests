package org.example.requesters.skeleton.interfaces;

import org.example.models.BaseModel;

public interface CrudEndpointInterface  {
    Object post(BaseModel requestBody);
    Object get(Integer id);
    Object update(Integer id, BaseModel requestBody);
    Object delete(Integer id);
}
