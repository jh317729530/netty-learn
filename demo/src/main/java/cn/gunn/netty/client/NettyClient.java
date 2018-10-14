package cn.gunn.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class NettyClient {

    public static final int MAX_RETRY = 5;

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                });

//        bootstrap.connect("192.168.2.142", 8000)
//                .addListener(future -> {
//                    if (future.isSuccess()) {
//                        System.out.println("connect success");
//                    } else {
//                        System.out.println("connect falid");
//                    }
//                });
//
//        Thread.sleep(100000);

        connect(bootstrap, "192.168.2.142", 8000, MAX_RETRY);
    }

    /**
     * connet to server,if connect fail,retry it
     *
     * @param host  server hostname
     * @param port  server port
     * @param retry retry times
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port)
                .addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("connect success");
                    } else if (retry == 0) {
                        System.err.println("retry connect " + retry + " times,quit!");
                    } else {
                        int order = (MAX_RETRY - retry) + 1;
                        int delay = 1 << order;
                        bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
                    }
                });

    }
}
