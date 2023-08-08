package ru.borisov.users.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.borisov.users.exception.error.Code;
import ru.borisov.users.exception.error.Error;
import ru.borisov.users.exception.error.ErrorResponse;


@ControllerAdvice
@Log4j2
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrorException(Exception ex) {
        ex.printStackTrace();
        log.error("internal server error: {}", ex::toString);
        return new ResponseEntity<>(ErrorResponse.builder()
                .error(Error.builder()
                        .code(Code.INTERNAL_SERVER_ERROR)
                        .message("Внутренняя ошибка сервиса")
                        .build())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("common error: {}", ex::toString);
        ex.printStackTrace(); // Вывод стека вызовов
        return new ResponseEntity<>(ErrorResponse.builder()
                .error(Error.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .build())
                .build(), ex.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied exception: {}", ex::toString);
        return new ResponseEntity<>(ErrorResponse.builder()
                .error(Error.builder()
                        .code(Code.ACCESS_DENIED)
                        .message("У Вас нет прав на выполнение данной операции")
                        .build())
                .build(), HttpStatus.FORBIDDEN);
    }
}
