package org.yqj.grpc.demo.document.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created by yaoqijun.
 * Date:2016-11-01
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class HelloWorldServer {

    // server grpc 定义方式
    private int port = 50051;
    private Server server;

    public static void main(String[] args) throws Exception {
        System.out.println("grpc server demo");
        final HelloWorldServer server = new HelloWorldServer();
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
                HelloWorldServer.this.stop();
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
            System.out.println(" start server say hello method");
            HelloReply helloReply = HelloReply.newBuilder()
                    .setMessage("hello " + request.getName())
                    .build();
            responseObserver.onNext(helloReply);
            responseObserver.onCompleted();
            System.out.println("finish server say hello method");
        }
    }
}
