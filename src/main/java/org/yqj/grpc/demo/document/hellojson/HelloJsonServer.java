package org.yqj.grpc.demo.document.hellojson;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.yqj.grpc.demo.document.helloworld.GreeterGrpc;
import org.yqj.grpc.demo.document.helloworld.HelloReply;
import org.yqj.grpc.demo.document.helloworld.HelloRequest;

import java.util.logging.Logger;

/**
 * Created by yaoqijun.
 * Date:2016-11-05
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class HelloJsonServer {

    public static final Logger logger = Logger.getLogger(HelloJsonServer.class.getName());

    private int port = 50051;
    private Server server;

    public static void main(String[] args) throws  Exception{
        final HelloJsonServer server = new HelloJsonServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws Exception{

        server = ServerBuilder.forPort(port).addService(new GreeterImpl())
                .build().start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                HelloJsonServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static final class GreeterImpl extends GreeterGrpc.GreeterImplBase{

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply helloReply = HelloReply.newBuilder()
                    .setMessage("hello "+request.getName())
                    .build();
            responseObserver.onNext(helloReply);
            responseObserver.onCompleted();
        }

        @Override
        public ServerServiceDefinition bindService() {
            return ServerServiceDefinition.builder(GreeterGrpc.getServiceDescriptor().getName())
                    .addMethod(HelloJsonClient.HelloJsonStub.METHOD_SAY_HELLO,
                            ServerCalls.asyncUnaryCall(new ServerCalls.UnaryMethod<HelloRequest, HelloReply>(){
                                public void invoke(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                                    sayHello(request, responseObserver);
                                }
                            }))
                    .build();
        }
    }

}
