package ru.borisov.users.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.borisov.users.exception.CommonException;
import ru.borisov.users.exception.error.Code;

import java.util.Set;

@Component
@Log4j2
@RequiredArgsConstructor
public class ValidationUtils {

    private final Validator validator;

    public <T> void validateRequest(T request) {

        if (request != null) {
            Set<ConstraintViolation<T>> result = validator.validate(request);
            if (!result.isEmpty()) {
                String resultValidations = result.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((s1, s2) -> s1 + ";\n" + s2)
                        .orElse("");
                log.error("Переданный в запросе json не валиден, ошибки валидации: {}", () -> resultValidations);

                throw CommonException.builder()
                        .code(Code.REQUEST_VALIDATION_ERROR)
                        .message(resultValidations)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }
    }
}
