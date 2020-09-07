package vn.duclm.demogrpc.core;

import com.google.rpc.BadRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CoreControllerAdvice extends ResponseEntityExceptionHandler {

    @Data
    @AllArgsConstructor
    static class BadRequest {
        String field;
        String description;
    }

    @Data
    @Builder
    static class ErrorMessage {
        private String message;
        private List<BadRequest> details;
    }

    @ExceptionHandler(CoreRuntimeException.class)
    public ResponseEntity<ErrorMessage> handlingException(CoreRuntimeException exception) {
        com.google.rpc.BadRequest brq = exception.getEx();
        com.google.rpc.BadRequest.FieldViolation fieldViolations = brq.getFieldViolations(0);
        List<BadRequest> detail = new ArrayList<>();
        detail.add(new BadRequest(fieldViolations.getField(), fieldViolations.getDescription()));
        ErrorMessage msg = new ErrorMessage(exception.getMessage(), detail);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }
}
