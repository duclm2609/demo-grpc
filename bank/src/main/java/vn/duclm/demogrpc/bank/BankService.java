package vn.duclm.demogrpc.bank;

import com.example.demo.lib.ErrorSwitchChannel;
import com.example.demo.lib.HelloReply;
import com.example.demo.lib.HelloRequest;
import com.example.demo.lib.UserServiceGrpc;
import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class BankService extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        if (request.getName().length() == 0) {
            BadRequest badRequest = BadRequest.newBuilder()
                    .addFieldViolations(BadRequest.FieldViolation.newBuilder()
                            .setField("name")
                            .setDescription("Name should not be empty")
                            .build())
                    .build();

            Status status = Status.newBuilder()
                    .setCode(Code.INVALID_ARGUMENT.getNumber())
                    .setMessage("Invalid hello request")
                    .addDetails(Any.pack(badRequest))
                    .build();
            responseObserver.onError(StatusProto.toStatusException(status));
        } else if (request.getName().equals("error")) {
            // Unrecoverable error, require human intervention to handle
            ErrorInfo errorInfo = ErrorInfo.newBuilder()
                    .setDomain("bank.service")
                    .setReason("bank service denial: payment data corruption")
                    .build();
            Status status = Status.newBuilder()
                    .setCode(Code.INTERNAL.getNumber())
                    .setMessage("payment payload rejection")
                    .addDetails(Any.pack(errorInfo))
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        } else if (request.getName().equals("switch")) {
            // Recoverable error, send more information for caller to retry
            ErrorSwitchChannel switchChannel = ErrorSwitchChannel.newBuilder()
                    .setNewUrl("http://1.2.3.4:6789")
                    .build();
            Status status = Status.newBuilder()
                    .setCode(Code.INTERNAL.getNumber())
                    .setMessage("Bank host changed")
                    .addDetails(Any.pack(switchChannel))
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        } else {
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello, " + request.getName())
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
