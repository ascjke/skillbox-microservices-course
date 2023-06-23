package ru.borisov.users.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.borisov.users.exception.error.Code;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonException extends RuntimeException {

    private Code code;
    private String message;
    private HttpStatus httpStatus;
}
