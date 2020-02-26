package com.why.test.netty.common;

import com.why.test.netty.sbe.baseline.CarEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class CustomEncoder extends MessageToMessageEncoder<CarEncoder> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CarEncoder aVoid, List<Object> list) throws Exception {

    }
}
