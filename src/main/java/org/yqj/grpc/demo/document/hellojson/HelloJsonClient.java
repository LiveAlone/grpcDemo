package org.yqj.grpc.demo.document.hellojson;

import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.ClientCalls;
import org.yqj.grpc.demo.document.helloworld.GreeterGrpc;
import org.yqj.grpc.demo.document.helloworld.HelloReply;
import org.yqj.grpc.demo.document.helloworld.HelloRequest;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by yaoqijun.
 * Date:2016-11-05
 * Email:yaoqijunmail@gmail.io
 * Descirbe:
 */
public class HelloJsonClient {

    public static final Logger logger = Logger.getLogger(HelloJsonClient.class.getName());

    public final ManagedChannel channel;
    public final HelloJsonStub helloJsonStub;

    public HelloJsonClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true).build();
        helloJsonStub = new HelloJsonStub(channel);
    }

    public void shutdown() throws Exception{
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sayHello(String name){
        HelloRequest helloRequest = HelloRequest.newBuilder().setName(name).build();
        HelloReply helloReply = null;
        try {
            helloReply = helloJsonStub.sayHello(helloRequest);
        }catch (Exception e){
            System.out.println("say hello error");
        }

        System.out.println("reply message is : " + helloReply.getMessage());
    }

    public static void main(String[] args) throws Exception {
        HelloJsonClient client = new HelloJsonClient("localhost", 50051);
        try {
            String user = "world";
            client.sayHello(user);
        } finally {
            client.shutdown();
        }
    }

    public static class HelloJsonStub extends AbstractStub<HelloJsonStub>{

        static final MethodDescriptor<HelloRequest, HelloReply> METHOD_SAY_HELLO =
                MethodDescriptor.create(
                        GreeterGrpc.METHOD_SAY_HELLO.getType(),
                        GreeterGrpc.METHOD_SAY_HELLO.getFullMethodName(),
                        ProtoUtils.jsonMarshaller(HelloRequest.getDefaultInstance()),
                        ProtoUtils.jsonMarshaller(HelloReply.getDefaultInstance()));

        protected HelloJsonStub(Channel channel) {
            super(channel);
        }

        protected HelloJsonStub(Channel channel, CallOptions callOptions){
            super(channel, callOptions);
        }

        protected HelloJsonStub build(Channel channel, CallOptions callOptions) {
            return new HelloJsonStub(channel, callOptions);
        }

        public HelloReply sayHello(HelloRequest request){
            return ClientCalls.blockingUnaryCall(getChannel(), METHOD_SAY_HELLO, getCallOptions(), request);
        }
    }

}
