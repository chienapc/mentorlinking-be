package vn.fpt.se18.MentorLinking_BackEnd.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.fpt.se18.MentorLinking_BackEnd.dto.response.BaseResponse;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor {
    private final MessageSource messageSource;

    //Bắt lỗi đã quy ước
    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Object>> handleAppException(AppException ex) {
        return ResponseEntity
                .badRequest()
                .body(setResponse(
                        String.valueOf(ex.getErrorCode().getCode()),
                        ex.getCustomMessage() != null ? ex.getCustomMessage() : ex.getErrorCode().getMessage()));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDeniedException() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(setResponse(String.valueOf(errorCode.getCode()), errorCode.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Object>> handleHttpRequestMethodNotSupportedException() {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(setResponse(String.valueOf(errorCode.getCode()), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodNotValid(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(setResponse(String.valueOf(errorCode.getCode()), enumKey));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNoResourceFound(NoResourceFoundException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_ENDPOINT;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(setResponse(String.valueOf(errorCode.getCode()), "Invalid endpoint " + ex.getResourcePath()));
    }

    //Bắt lỗi chưa xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(Exception ex) {
        log.error("RuntimeException: ", ex);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(setResponse(String.valueOf(errorCode.getCode()), errorCode.getMessage()));
    }

    private BaseResponse<Object> setResponse(String errorCode, String description) {
        BaseResponse<Object> responseBody = new BaseResponse<>();
        responseBody.setRespCode(errorCode);
        responseBody.setDescription(messageSource.getMessage(description, null, description, LocaleContextHolder.getLocale()));
        return responseBody;
    }

}
