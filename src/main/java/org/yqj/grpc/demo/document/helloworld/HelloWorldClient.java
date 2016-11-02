package org.yqj.grpc.demo.document.helloworld;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

/**
 * Created by yaoqijun.
 * Date:2016-11-01
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class HelloWorldClient {

    private final ManagedChannel managedChannel;
    private final GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    public HelloWorldClient(String name, Integer port){
        managedChannel = ManagedChannelBuilder.forAddress(name, port).usePlaintext(true).build();
        greeterBlockingStub = GreeterGrpc.newBlockingStub(managedChannel);
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(String name) {
        System.out.println("say greet to server");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = greeterBlockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        HelloWorldClient client = new HelloWorldClient("localhost", 50051);
        try {
            String user = "yaoqijun";
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
