package cn.gunn.netty.client;


import cn.gunn.netty.handler.RedisChannelDuplexHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class RedisClient {

    private Channel channel;

    public static final byte[] CRLF = "\r\n".getBytes();



    @Before
    public void setUp() throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new RedisChannelDuplexHandler());
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("39.108.37.28", 6379);

        channel = channelFuture.sync().channel();

        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeByte('*');
        buffer.writeCharSequence(Long.toString(2), CharsetUtil.US_ASCII);
        buffer.writeBytes(CRLF);
        buffer.writeByte('$');
        buffer.writeCharSequence(Long.toString(6), CharsetUtil.US_ASCII);
        buffer.writeBytes(CRLF);
        buffer.writeBytes("select".getBytes(CharsetUtil.UTF_8));
        buffer.writeBytes(CRLF);
        buffer.writeByte('$');
        buffer.writeCharSequence(Long.toString(1), CharsetUtil.US_ASCII);
        buffer.writeBytes(CRLF);
        buffer.writeBytes("2".getBytes(CharsetUtil.UTF_8));
        buffer.writeBytes(CRLF);

        channel.writeAndFlush(buffer);
    }


    @Test
    public void testConnectRedis() throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new RedisChannelDuplexHandler());
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("39.108.37.28", 6379);

        Channel channel = channelFuture.sync().channel();

        assert null != channel;
        assert channel.isActive();
    }

    @Test
    public void testSet() throws InterruptedException, ExecutionException {
        ByteBuf writeBuffer = channel.alloc().buffer();
        writeBuffer.writeByte('*');
        writeBuffer.writeCharSequence(Long.toString(3), CharsetUtil.US_ASCII);
        writeBuffer.writeBytes(CRLF);
        writeBuffer.writeByte('$');
        writeBuffer.writeCharSequence(Long.toString(3), CharsetUtil.US_ASCII);
        writeBuffer.writeBytes(CRLF);
        writeBuffer.writeBytes("set".getBytes(CharsetUtil.UTF_8));
        writeBuffer.writeBytes(CRLF);
        writeBuffer.writeByte('$');
        writeBuffer.writeCharSequence(Long.toString(4), CharsetUtil.US_ASCII);
        writeBuffer.writeBytes(CRLF);
        writeBuffer.writeBytes("name".getBytes(CharsetUtil.UTF_8));
        writeBuffer.writeBytes(CRLF);
        writeBuffer.writeByte('$');
        writeBuffer.writeCharSequence(Long.toString(10), CharsetUtil.US_ASCII);
        writeBuffer.writeBytes(CRLF);
        writeBuffer.writeBytes("helloworld".getBytes(CharsetUtil.UTF_8));
        writeBuffer.writeBytes(CRLF);
        channel.writeAndFlush(writeBuffer).sync().get();
    }

    @Test
    public void testGet() throws InterruptedException, ExecutionException {
        ByteBuf get = channel.alloc().buffer();
        get.writeByte('*');
        get.writeCharSequence(Long.toString(2), CharsetUtil.US_ASCII);
        get.writeBytes(CRLF);
        get.writeByte('$');
        get.writeCharSequence(Long.toString(3), CharsetUtil.US_ASCII);
        get.writeBytes(CRLF);
        get.writeBytes("get".getBytes(CharsetUtil.UTF_8));
        get.writeBytes(CRLF);
        get.writeByte('$');
        get.writeCharSequence(Long.toString(4), CharsetUtil.US_ASCII);
        get.writeBytes(CRLF);
        get.writeBytes("name".getBytes(CharsetUtil.UTF_8));
        get.writeBytes(CRLF);

        byte[] bytes = new byte[get.readableBytes()];
        get.getBytes(get.readerIndex(), bytes);
        System.out.println(new String(bytes, 0, get.readableBytes()));

        Promise<String> promise = ImmediateEventExecutor.INSTANCE.newPromise();
        AttributeKey<Promise<String>> result = AttributeKey.newInstance("result");
        channel.attr(result).set(promise);

        channel.writeAndFlush(get).sync().get();

        promise.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("result:"+future.getNow());
            }
        });


        Thread.sleep(1000);
    }
}
