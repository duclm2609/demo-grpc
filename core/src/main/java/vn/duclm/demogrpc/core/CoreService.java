package vn.duclm.demogrpc.core;

import com.example.demo.lib.ErrorSwitchChannel;
import com.example.demo.lib.HelloReply;
import com.example.demo.lib.HelloRequest;
import com.example.demo.lib.UserServiceGrpc;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoreService {

    @GrpcClient("local-grpc-server")
    private UserServiceGrpc.UserServiceBlockingStub bankService;

    public String callBankService(String hello) {
        HelloRequest helloRequest = HelloRequest.newBuilder()
                .setName(hello)
                .build();
        try {
            HelloReply reply = this.bankService.sayHello(helloRequest);
            return reply.getMessage();
        } catch (Exception e) {
            var status = StatusProto.fromThrowable(e);
            if (status.getCode() == Code.INVALID_ARGUMENT.getNumber()) {
                Any details = status.getDetails(0);
                if (details.is(BadRequest.class)) {
                    try {
                        BadRequest unpack = details.unpack(BadRequest.class);
                        throw new CoreRuntimeException(status.getMessage(), unpack);
                    } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                        invalidProtocolBufferException.printStackTrace();
                    }
                }
                log.error(details.toString());
            } else if (status.getCode() == Code.INTERNAL.getNumber()) {
                Any details = status.getDetails(0);
                if (details.is(ErrorInfo.class)) {
                    try {
                        ErrorInfo unpack = details.unpack(ErrorInfo.class);
                        // Unrecoverable error, require human intervention
                        // Log the error for debugging
                        log.error("call bank service errror: {}", unpack.toString());
                        throw new RuntimeException("bank service error");
                    } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                        invalidProtocolBufferException.printStackTrace();
                    }
                } else if (details.is(ErrorSwitchChannel.class)) {
                    try {
                        ErrorSwitchChannel switchChannel = details.unpack(ErrorSwitchChannel.class);
                        // Recoverable error, can handle automatically
                        return "Retry with new url: " + switchChannel.getNewUrl();
                    } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                        invalidProtocolBufferException.printStackTrace();
                    }
                }
                log.error(details.toString());
            }
        }
        return "";
    }
}
