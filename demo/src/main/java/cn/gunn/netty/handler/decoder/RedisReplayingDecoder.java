package cn.gunn.netty.handler.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class RedisReplayingDecoder extends ReplayingDecoder<Void> {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("response");
        int i = byteBuf.readableBytes();
    }
}
