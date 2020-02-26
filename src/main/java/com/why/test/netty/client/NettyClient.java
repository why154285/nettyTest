package com.why.test.netty.client;

import com.why.test.netty.DefaultConstants;
import com.why.test.netty.common.NettyMessageDecoder;
import com.why.test.netty.common.NettyMessageEncoder;
import com.why.test.netty.common.SbeEncoder;
import com.why.test.netty.sbe.baseline.CarEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.UnsafeBuffer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * @description: 客户端
 **/

@Slf4j
@Data
public class NettyClient implements Runnable{

    static final String HOST = System.getProperty("host", DefaultConstants.DefaultIntAddress);
    static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private String content;

    public NettyClient(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            int num = 0;
            boolean boo =true;

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyClientChannelInitializer() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                            p.addLast("decoder", new StringDecoder());
//                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new NettyMessageDecoder());
                            p.addLast(new NettyMessageEncoder());
//                            p.addLast("encoder", new SbeEncoder());
//                            p.addLast(new NettyClientHandler());
                        }
                    });


            // 发起异步连接操作
            ChannelFuture future = b.connect(HOST, PORT).sync();

            while (boo) {

                num++;
                CarEncoder carEncoder = new CarEncoder();
                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);

                final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
                NettyMessageEncoder.encode(carEncoder,directBuffer);
//                future.channel().writeAndFlush(content + "--" + new Date());
                future.channel().writeAndFlush(carEncoder);


                try { //休眠一段时间
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //每一条线程向服务端发送的次数
                if (num == 100) {
                    boo = false;
                }
            }

            log.info(content + "-----------------------------" + num);
//            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     *  下面是不加线程的
     */
    /*public static void main(String[] args) throws Exception {

        sendMessage("hhh你好？");
    }

    public static void sendMessage(String content) throws InterruptedException {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyClientChannelInitializer() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new NettyClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.channel().writeAndFlush(content);
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }*/
}
