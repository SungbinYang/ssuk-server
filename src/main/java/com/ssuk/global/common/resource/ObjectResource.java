package com.ssuk.global.common.resource;

import org.springframework.hateoas.EntityModel;

public class ObjectResource extends EntityModel<Object> {

    public ObjectResource(Object content) {
        super(content);
    }
}
