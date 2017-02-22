package org.yqj.grpc.demo.document.steamroute;

import com.google.common.collect.Lists;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.yqj.grpc.demo.document.routeguide.MessageNode;
import org.yqj.grpc.demo.document.routeguide.MessageResponse;
import org.yqj.grpc.demo.document.routeguide.RouteGuideGrpc;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by yaoqijun.
 * Date:2016-11-05
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class RouteStreamClient {

    private final ManagedChannel channel;
    private final RouteGuideGrpc.RouteGuideBlockingStub routeGuideBlockingStub;
    private final RouteGuideGrpc.RouteGuideStub routeGuideStub;

    List<String> clientTest = Lists.newArrayList("yao", "qi", "jun");

    public static void main(String[] args) throws Exception {
        System.out.println("this test stream");
        RouteStreamClient client = new RouteStreamClient();
//        client.sayHelloTest();
//        client.sayHelloStream();
//        client.sayStreamHello();
        client.sayStreamHelloStream();
    }

    public void sayStreamHelloStream() throws Exception{
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<MessageNode> requestObserver = routeGuideStub.sayStreamHelloStream(new StreamObserver<MessageResponse>() {
            @Override
            public void onNext(MessageResponse value) {
                System.out.println("finish response is : " + value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        });

        clientTest.forEach(s-> requestObserver.onNext(MessageNode.newBuilder().setMessage(s).build()));
        requestObserver.onCompleted();
        finishLatch.await(1, TimeUnit.MINUTES);
    }

    public RouteStreamClient(){
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        routeGuideBlockingStub = RouteGuideGrpc.newBlockingStub(channel);
        routeGuideStub = RouteGuideGrpc.newStub(channel);
    }

    public void sayStreamHello() throws Exception {
        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver streamObserver =  new StreamObserver<MessageResponse>(){
            @Override
            public void onNext(MessageResponse value) {
                System.out.println("*** stream observer starter " + value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        StreamObserver<MessageNode> responseStreamObserver = routeGuideStub.sayStreamHello(streamObserver);

        clientTest.forEach(s -> responseStreamObserver.onNext(MessageNode.newBuilder().setMessage(s).build()));

        responseStreamObserver.onCompleted();

        finishLatch.await(1, TimeUnit.MINUTES);
    }

    public void sayHelloStream(){
        System.out.println("*** prepare hello stream ");
        Iterator<MessageResponse> responses = routeGuideBlockingStub.sayHelloStream(MessageNode.newBuilder().setMessage("yaoqijun").build());
        while (responses.hasNext()){
            System.out.println("response content is : " + responses.next().getResponse());
        }
        System.out.println("*** finish hello stream");
    }

    public void sayHelloTest(){
        System.out.println("*** prepare to say hello");
        MessageResponse response = routeGuideBlockingStub.sayHello(MessageNode.newBuilder()
                .setMessage("yaoqijun")
                .build());
        System.out.println("*** response message is "+response.getResponse());
        System.out.println("*** finish to say hello");
    }

}
