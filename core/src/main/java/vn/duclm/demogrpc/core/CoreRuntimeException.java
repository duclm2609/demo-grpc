package vn.duclm.demogrpc.core;

import com.google.rpc.BadRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//@AllArgsConstructor
@Component
public class CoreRuntimeException extends RuntimeException {
    BadRequest ex;

    public CoreRuntimeException() {
        super();
    }

    public CoreRuntimeException(String message) {
        super(message);
    }

    public CoreRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreRuntimeException(Throwable cause) {
        super(cause);
    }

    protected CoreRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CoreRuntimeException(String message, BadRequest ex) {
        super(message);
        this.ex = ex;
    }

    public BadRequest getEx() {
        return ex;
    }

    public void setEx(BadRequest ex) {
        this.ex = ex;
    }
}
