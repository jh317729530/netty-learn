package cn.gunn.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class RedisChannelDuplexHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        int code = in.readByte();
        if (code == '+') {
            int len = in.bytesBefore((byte) '\r');
            String resultString = in.toString(in.readerIndex(), len, CharsetUtil.UTF_8);
            in.skipBytes(len + 2);
            Promise<String> result = (Promise<String>) ctx.channel().attr(AttributeKey.valueOf("result")).get();
            result.trySuccess(resultString);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
