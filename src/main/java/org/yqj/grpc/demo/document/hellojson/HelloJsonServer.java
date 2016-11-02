package org.yqj.grpc.demo.document.hellojson;

import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.yqj.grpc.demo.document.helloworld.GreeterGrpc;
import org.yqj.grpc.demo.document.helloworld.HelloReply;
import org.yqj.grpc.demo.document.helloworld.HelloRequest;

import static io.grpc.stub.ServerCalls.asyncUnaryCall;

/**
 * Created by yaoqijun.
 * Date:2016-11-01
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class HelloJsonServer {

    // server grpc 定义方式
    private int port = 50051;
    private Server server;

    public static void main(String[] args) throws Exception {
        System.out.println("grpc server demo");
        final HelloJsonServer server = new HelloJsonServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private void start() throws Exception{
        server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();
        System.out.println(" server start to listener port");

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println(" jvm to stop, shut down hook ");
                HelloJsonServer.this.stop();
                System.out.println(" stop jvm , and hook ");
            }
        });
    }

    private void stop(){
        if (server != null){
            server.shutdown();
        }
    }

    private class GreeterImpl extends GreeterGrpc.GreeterImplBase{

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply helloReply = HelloReply.newBuilder()
                    .setMessage("hello " + request.getName())
                    .build();
            responseObserver.onNext(helloReply);
            responseObserver.onCompleted();
        }

        @Override
        public ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition
                    .builder(GreeterGrpc.getServiceDescriptor().getName())
                    .addMethod(HelloJsonClient.HelloJsonStub.METHOD_SAY_HELLO,
                            asyncUnaryCall(
                                    new ServerCalls.UnaryMethod<HelloRequest, HelloReply>() {
                                        public void invoke(
                                                HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                                            sayHello(request, responseObserver);
                                        }
                                    }))
                    .build();
        }

    }
}
