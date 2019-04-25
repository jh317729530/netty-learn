package cn.gunn.netty.handler;

import cn.gunn.netty.future.GFuture;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

import java.io.IOException;

public class RedisChannelDuplexHandler extends ChannelDuplexHandler {

    public static final byte CR = '\r';

    public static final byte LF = '\n';

    public static final char ZERO = '0';

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;

        byte[] bytes = new byte[in.readableBytes()];
        in.getBytes(in.readerIndex(), bytes);
        String str = new String(bytes, 0, in.readableBytes());
//        System.out.println(str);

        int code = in.readByte();
        if (code == '+') {
//            int len = in.bytesBefore((byte) '\r');
//            String resultString = in.toString(in.readerIndex(), len, CharsetUtil.UTF_8);
//            in.skipBytes(len + 2);
//            Promise<String> result = (Promise<String>) ctx.channel().attr(AttributeKey.valueOf("result")).get();
//            result.trySuccess(resultString);
        } else if (code == '$') {
            ByteBuf buf = readBytes(in);
            GFuture<String> result = (GFuture<String>) ctx.channel().attr(AttributeKey.valueOf("result")).get();
            result.trySuccess(str);
        }
    }

    private ByteBuf readBytes(ByteBuf is) throws IOException {
        long l = readLong(is);
        if (l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Java only supports arrays up to " + Integer.MAX_VALUE + " in size");
        }
        int size = (int) l;
        if (size == -1) {
            return null;
        }
        ByteBuf buffer = is.readSlice(size);
        int cr = is.readByte();
        int lf = is.readByte();
        if (cr != CR || lf != LF) {
            throw new IOException("Improper line ending: " + cr + ", " + lf);
        }
        return buffer;
    }

    private long readLong(ByteBuf is) throws IOException {
        long size = 0;
        int sign = 1;
        int read = is.readByte();
        if (read == '-') {
            read = is.readByte();
            sign = -1;
        }
        do {
            if (read == CR) {
                if (is.readByte() == LF) {
                    break;
                }
            }
            int value = read - ZERO;
            if (value >= 0 && value < 10) {
                size *= 10;
                size += value;
            } else {
                throw new IOException("Invalid character in integer");
            }
            read = is.readByte();
        } while (true);
        return size * sign;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
