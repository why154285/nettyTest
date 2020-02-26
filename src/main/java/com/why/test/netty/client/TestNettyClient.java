package com.why.test.netty.client;

import com.why.test.netty.sbe.baseline.CarDecoder;

/**
 * @description: 模拟多客户端发送报文
 **/
public class TestNettyClient {

    public static void main(String[] args) {

        //开启10条线程，每条线程就相当于一个客户端
        for (int i = 1; i <= 10; i++) {

            new Thread(new NettyClient("thread" + "--" + i)).start();
        }

        CarDecoder carDecoder = new CarDecoder();
        carDecoder.engine()
                .capacity();
        CarDecoder.PerformanceFiguresDecoder performanceFiguresDecoder = carDecoder.performanceFigures();
        performanceFiguresDecoder.next();
    }
}
