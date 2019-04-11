package cn.gunn.netty.client;


import cn.gunn.netty.handler.FirstServerHandler;
import cn.gunn.netty.handler.RedisChannelDuplexHandler;
import cn.gunn.netty.handler.decoder.RedisReplayingDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.nio.charset.Charset;

public class RedisClient {


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


        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = channelFuture.channel();
                ByteBuf buffer = channel.alloc().buffer();


                buffer.writeByte('*');
                buffer.writeCharSequence(Long.toString(2), Charset.forName("US-ASCII"));
                buffer.writeBytes("\r\n".getBytes());
                buffer.writeByte('$');
                buffer.writeCharSequence(Long.toString(6), Charset.forName("US-ASCII"));
                buffer.writeBytes("\r\n".getBytes());
                buffer.writeBytes("select".getBytes(Charset.forName("UTF-8")));
                buffer.writeBytes("\r\n".getBytes());
                buffer.writeByte('$');
                buffer.writeCharSequence(Long.toString(1), CharsetUtil.US_ASCII);
                buffer.writeBytes("\r\n".getBytes());
                buffer.writeBytes("1".getBytes(Charset.forName("UTF-8")));
                buffer.writeBytes("\r\n".getBytes());


                ChannelFuture selectFuture = channel.writeAndFlush(buffer);
                selectFuture.addListener(sFuture -> {
                    if (sFuture.isSuccess()) {
                        ByteBuf writeBuffer = channel.alloc().buffer();
//                        String writeStr = "*3\\r\\n$3\\r\\nset\\r\\n$4\\r\\nname\\r\\n$10\\r\\nhelloworld\\r\\n";

                        writeBuffer.writeByte('*');
                        writeBuffer.writeCharSequence(Long.toString(3), Charset.forName("US-ASCII"));
                        writeBuffer.writeBytes("\r\n".getBytes());
                        writeBuffer.writeByte('$');
                        writeBuffer.writeCharSequence(Long.toString(3), Charset.forName("US-ASCII"));
                        writeBuffer.writeBytes("\r\n".getBytes());
                        writeBuffer.writeBytes("set".getBytes(Charset.forName("UTF-8")));
                        writeBuffer.writeBytes("\r\n".getBytes());
                        writeBuffer.writeByte('$');
                        writeBuffer.writeCharSequence(Long.toString(4), Charset.forName("US-ASCII"));
                        writeBuffer.writeBytes("\r\n".getBytes());
                        writeBuffer.writeBytes("name".getBytes(Charset.forName("UTF-8")));
                        writeBuffer.writeBytes("\r\n".getBytes());
                        writeBuffer.writeByte('$');
                        writeBuffer.writeCharSequence(Long.toString(10), Charset.forName("US-ASCII"));
                        writeBuffer.writeBytes("\r\n".getBytes());
                        writeBuffer.writeBytes("helloworld".getBytes(Charset.forName("UTF-8")));
                        writeBuffer.writeBytes("\r\n".getBytes());

                        byte[] bytes = new byte[writeBuffer.readableBytes()];
                        writeBuffer.getBytes(writeBuffer.readerIndex(), bytes);
                        System.out.println(new String(bytes,0,bytes.length));


                        channel.writeAndFlush(writeBuffer).addListener(writeFuture -> {
                            if (writeFuture.isSuccess()) {
                                System.out.println("sss");
                            } else {
                                Throwable cause = writeFuture.cause();
                                cause.printStackTrace();

                            }
                        });
                    } else {
                        Throwable cause = future.cause();
                        cause.printStackTrace();
                    }
                });
            }
        });




        Thread.sleep(1000000);
    }

}
