package vn.fpt.se18.MentorLinking_BackEnd.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED("01", "UNCATEGORIZED", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED("02", "METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED),
    UNAUTHORIZED("03", "UNAUTHORIZED", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED("04", "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    INVALID_INPUT("05", "INVALID_INPUT", HttpStatus.BAD_REQUEST),
    ERROR_PATH("06", "ERROR_PATH", HttpStatus.BAD_REQUEST),
    INVALID_ENDPOINT("07", "INVALID_ENDPOINT", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
