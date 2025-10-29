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

    // Role & Permission Error Codes
    ROLE_NOT_FOUND("08", "Vai trò không tồn tại", HttpStatus.NOT_FOUND),
    ROLE_CODE_EXISTED("09", "Mã vai trò đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NAME_EXISTED("10", "Tên vai trò đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_IN_USE("11", "Vai trò đang được sử dụng, không thể xóa", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND("12", "Quyền không tồn tại", HttpStatus.NOT_FOUND),
    PERMISSION_NAME_EXISTED("13", "Tên quyền đã tồn tại", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
