package ru.borisov.users.exception;

import lombok.*;
import org.springframework.http.HttpStatus;
import ru.borisov.users.exception.error.Code;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonException extends RuntimeException {

    private Code code;
    private String message;
    private HttpStatus httpStatus;
}
