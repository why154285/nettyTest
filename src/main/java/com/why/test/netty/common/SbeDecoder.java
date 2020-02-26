package com.why.test.netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SbeDecoder extends ByteToMessageDecoder {
    private static final int MIN_LENGTH = 2+2+2;	//数据内容为空时最小长度 即(协议头、版本号、消息长度（长度=0时消息最短）)
    private static final int MAX_LENGTH = 2048;		//约定数据最大长度(防止socket字节流攻击)

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        try {
            if(in.readableBytes()<MIN_LENGTH) {//可读取字节数少于最小长度
                return;
            }
            if(in.readableBytes() > MAX_LENGTH){//可读字节数大于约定最大长度
                in.skipBytes(in.readableBytes());
                return;
            }
            while(true) {
                in.markReaderIndex();
//                if(in.readShort() == Message.TAG){//寻找协议头
//                    if(in.readShort()==Message.VERSION) {
//                        break;
//                    }
//                }
                in.resetReaderIndex();
                in.readByte();
                if(in.readableBytes() <= MIN_LENGTH){
                    return;
                }
                if(in.readableBytes() > MAX_LENGTH){
                    in.skipBytes(in.readableBytes());
                    return;
                }
            }
//            Message message = new Message();
//            int length = in.readInt();
//            message.setLENGTH(length);
//            byte[] data = new byte[length-8];
//            in.readBytes(data);
//            message.setDATA(new String(data,"ISO-8859-1"));
//            out.add(message);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}
