package cn.gunn.netty.client;

import cn.gunn.netty.handler.FirstClientHandler;
import cn.gunn.netty.handler.FirstServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class MyClientDemo {

    private static Channel channel;

    static {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter());
                    }
                });
        try {
            channel = bootstrap.connect("localhost", 8000).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void send() throws InterruptedException {
        while (true) {
            ByteBuf buffer = channel.alloc().buffer();
            buffer.writeChar(2);
            channel.writeAndFlush(buffer);
            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MyClientDemo();
        send();
    }
}
