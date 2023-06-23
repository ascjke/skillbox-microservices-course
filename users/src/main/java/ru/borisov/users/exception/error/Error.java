package ru.borisov.users.exception.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {

    private Code code;
    private String message;
}
