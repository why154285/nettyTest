package com.why.test.netty.common;

import com.why.test.netty.sbe.baseline.CarEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SbeEncoder extends MessageToByteEncoder<CarEncoder> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CarEncoder carEncoder, ByteBuf byteBuf) throws Exception {
        System.out.printf("carEncoder ========================  begin");
        carEncoder.extras()
                .clear()
                .cruiseControl(true)
                .sportsPack(true)
                .sunRoof(false);
        byteBuf.writeBytes(carEncoder.toString().getBytes());
        System.out.printf(carEncoder.toString());
        System.out.printf("carEncoder ========================  end");
    }
}
