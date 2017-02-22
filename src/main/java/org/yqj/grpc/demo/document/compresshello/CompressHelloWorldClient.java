package org.yqj.grpc.demo.document.compresshello;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.yqj.grpc.demo.document.helloworld.GreeterGrpc;
import org.yqj.grpc.demo.document.helloworld.HelloReply;
import org.yqj.grpc.demo.document.helloworld.HelloRequest;

import java.util.concurrent.TimeUnit;

/**
 * Created by yaoqijun.
 * Date:2016-11-01
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class CompressHelloWorldClient {

    private final ManagedChannel managedChannel;
    private final GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    public CompressHelloWorldClient(String name, Integer port){
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
            response = greeterBlockingStub.withCompression("gzip").sayHello(request);
        } catch (StatusRuntimeException e) {
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        CompressHelloWorldClient client = new CompressHelloWorldClient("localhost", 50051);
        try {
            String user = "yaoqijun";
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
