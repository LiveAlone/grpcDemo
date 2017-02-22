package org.yqj.grpc.demo.document.steamroute;

import com.google.common.collect.Lists;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.yqj.grpc.demo.document.routeguide.MessageNode;
import org.yqj.grpc.demo.document.routeguide.MessageResponse;
import org.yqj.grpc.demo.document.routeguide.RouteGuideGrpc;

import java.util.List;

/**
 * Created by yaoqijun.
 * Date:2016-11-05
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class RouteStreamServer {

    public static final List<String> requestHeader = Lists.newArrayList(
            "yao hello : ","qi hello : ","jun hello : ","test hello : ");

    int port = 50051;
    Server server ;

    public void start() throws Exception{
        server = ServerBuilder.forPort(port)
                .addService(new RouteGuideService())
                .build().start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                RouteStreamServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        RouteStreamServer server = new RouteStreamServer();
        server.start();
        server.blockUntilShutdown();
    }


    public static class RouteGuideService extends RouteGuideGrpc.RouteGuideImplBase {
        @Override
        public void sayHello(MessageNode request, StreamObserver<MessageResponse> responseObserver) {

            System.out.println("*** server get message content");
            MessageResponse messageResponse = MessageResponse.newBuilder()
                    .setResponse("hello : "+request.getMessage())
                    .build();
            responseObserver.onNext(messageResponse);
            responseObserver.onCompleted();
            System.out.println("*** server finish response message content");
        }

        @Override
        public void sayHelloStream(MessageNode request, StreamObserver<MessageResponse> responseObserver) {
            System.out.println("*** start stream message response");
            for (String s : requestHeader) {
                responseObserver.onNext(MessageResponse.newBuilder()
                        .setResponse(s + request.getMessage())
                        .build());
            }
            responseObserver.onCompleted();
            System.out.println("*** finish stream message response");
        }

        @Override
        public StreamObserver<MessageNode> sayStreamHello(final StreamObserver<MessageResponse> responseObserver) {
            return new StreamObserver<MessageNode>() {

                private int index = 0;

                private String currentMessage = "";

                public void onNext(MessageNode value) {
                    currentMessage += value.getMessage();
                    index ++ ;
                }

                public void onError(Throwable t) {
                    System.out.println("stream request test " + t.toString());
                }

                public void onCompleted() {
                    responseObserver.onNext(MessageResponse.newBuilder()
                            .setResponse(requestHeader.get(index) + currentMessage)
                            .build());
                    responseObserver.onCompleted();
                }
            };

        }

        @Override
        public StreamObserver<MessageNode> sayStreamHelloStream(final StreamObserver<MessageResponse> responseObserver) {

            return new StreamObserver<MessageNode>() {
                public void onNext(MessageNode value) {
                    for (String s : requestHeader) {
                        responseObserver.onNext(MessageResponse.newBuilder()
                                .setResponse(s + value.getMessage())
                                .build());
                    }
                }

                public void onError(Throwable t) {
                    System.out.println("streaming request error");
                }

                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };

        }
    }


}
