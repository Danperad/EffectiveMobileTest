package ru.danperad.effectivemobiletest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateUniqueKeyException extends RuntimeException {
    public DuplicateUniqueKeyException() {
        super();
    }
}
