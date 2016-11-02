package org.yqj.grpc.demo.document.hellojson;

import static io.grpc.stub.ClientCalls.blockingUnaryCall;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.AbstractStub;
import org.yqj.grpc.demo.document.helloworld.GreeterGrpc;
import org.yqj.grpc.demo.document.helloworld.HelloReply;
import org.yqj.grpc.demo.document.helloworld.HelloRequest;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HelloJsonClient {

  private final ManagedChannel channel;
  private final HelloJsonStub blockingStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public HelloJsonClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true)
        .build();
    blockingStub = new HelloJsonStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void greet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      return;
    }
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    HelloJsonClient client = new HelloJsonClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String user = "world";
      if (args.length > 0) {
        user = args[0]; /* Use the arg as the name to greet if provided */
      }
      client.greet(user);
    } finally {
      client.shutdown();
    }
  }

  static final class HelloJsonStub extends AbstractStub<HelloJsonStub> {

    static final MethodDescriptor<HelloRequest, HelloReply> METHOD_SAY_HELLO =
        MethodDescriptor.create(
            GreeterGrpc.METHOD_SAY_HELLO.getType(),
            GreeterGrpc.METHOD_SAY_HELLO.getFullMethodName(),
            ProtoUtils.jsonMarshaller(HelloRequest.getDefaultInstance()),
            ProtoUtils.jsonMarshaller(HelloReply.getDefaultInstance()));

    protected HelloJsonStub(Channel channel) {
      super(channel);
    }

    protected HelloJsonStub(Channel channel, CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected HelloJsonStub build(Channel channel, CallOptions callOptions) {
      return new HelloJsonStub(channel, callOptions);
    }

    public HelloReply sayHello(HelloRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SAY_HELLO, getCallOptions(), request);
    }
  }
}

