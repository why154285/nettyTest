package com.why.test.netty;

import com.why.test.netty.server.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;

@Slf4j
@SpringBootApplication
public class TestNettyApplication implements CommandLineRunner {
//    private static final Logger log = LoggerFactory.getLogger(TestNettyApplication.class);

    @Autowired
    private NettyServer nettyServer;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(TestNettyApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(DefaultConstants.DefaultIntAddress,DefaultConstants.DefaultPort);
        log.info("netty服务器启动地址："+DefaultConstants.DefaultIntAddress);
        nettyServer.start(address);
    }
}
