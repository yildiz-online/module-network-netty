//        This file is part of the Yildiz-Online project, licenced under the MIT License
//        (MIT)
//
//        Copyright (c) 2016 Grégory Van den Borre
//
//        More infos available: http://yildiz.bitbucket.org
//
//        Permission is hereby granted, free of charge, to any person obtaining a copy
//        of this software and associated documentation files (the "Software"), to deal
//        in the Software without restriction, including without limitation the rights
//        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//        copies of the Software, and to permit persons to whom the Software is
//        furnished to do so, subject to the following conditions:
//
//        The above copyright notice and this permission notice shall be included in all
//        copies or substantial portions of the Software.
//
//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.

package be.yildiz.module.network.netty.factory;

import be.yildiz.module.network.netty.DecoderEncoder;
import be.yildiz.module.network.netty.HandlerFactory;
import be.yildiz.module.network.netty.NettyChannelInitializer;
import be.yildiz.module.network.netty.client.ClientNetty;
import be.yildiz.module.network.netty.client.SimpleClientHandlerFactory;
import be.yildiz.module.network.netty.client.SimpleClientNetty;
import be.yildiz.module.network.netty.client.WebSocketClientNetty;
import be.yildiz.module.network.netty.server.HttpStaticFileServerHandlerFactory;
import be.yildiz.module.network.netty.server.ServerNetty;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;

/**
 * Create server or client Netty implementations.
 *
 * @author Grégory Van den Borre
 */
public interface NettyFactory {

    /**
     * Create a new client for Netty.
     *
     * @return A client implementation.
     */
    static ClientNetty createClientNetty() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class);
        ClientNetty client = new WebSocketClientNetty(bootstrap);
        bootstrap.handler(new NettyChannelInitializer(new SimpleClientHandlerFactory(client, DecoderEncoder.WEBSOCKET)));
        return client;
    }

    /**
     * Create a new client for Netty.
     *
     * @return A client implementation.
     */
    static ClientNetty createSimpleClientNetty() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class);
        ClientNetty client = new SimpleClientNetty(bootstrap);
        bootstrap.handler(new NettyChannelInitializer(new SimpleClientHandlerFactory(client, DecoderEncoder.STRING)));
        return client;
    }

    /**
     * Create a new server for Netty.
     *
     * @param port    Port number.
     * @param factory Factory to create logic handlers.
     * @return A server implementation.
     */
    static ServerNetty createServerNetty(final int port, final HandlerFactory factory) {
        ChannelInitializer<SocketChannel> initializer = new NettyChannelInitializer(factory);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bs = new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(initializer);

        return ServerNetty.fromPort(bs, port);
    }

    /**
     * Create a new server for Netty.
     *
     * @param address Address where the socket will be created(can be null).
     * @param port    Port number.
     * @param factory Factory to create logic handlers.
     * @return A server implementation.
     */
    static ServerNetty createServerNetty(final String address, final int port, HandlerFactory factory) {
        ChannelInitializer<SocketChannel> initializer = new NettyChannelInitializer(factory);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bs = new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(initializer);

        return ServerNetty.fromAddress(bs, address, port);
    }

    /**
     * Create a new http server for Netty.
     *
     * @param port           Port number.
     * @param forbiddenFiles Resources not allowed to be delivered.
     * @return A server implementation.
     */
    static ServerNetty createHttpServerNetty(final int port, final List<String> forbiddenFiles) {
        return createServerNetty(port, new HttpStaticFileServerHandlerFactory(forbiddenFiles));
    }

}
