package ru.borisov.users.exception.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private Error error;
}
