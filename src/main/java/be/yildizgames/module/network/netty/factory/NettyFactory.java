/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2018 Grégory Van den Borre
 *
 *  More infos available: https://www.yildiz-games.be
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 *
 */

package be.yildizgames.module.network.netty.factory;

import be.yildizgames.module.network.DecoderEncoder;
import be.yildizgames.module.network.client.Client;
import be.yildizgames.module.network.netty.HandlerFactory;
import be.yildizgames.module.network.netty.NettyChannelInitializer;
import be.yildizgames.module.network.netty.client.ClientNetty;
import be.yildizgames.module.network.netty.client.SimpleClientHandlerFactory;
import be.yildizgames.module.network.netty.client.SimpleClientNetty;
import be.yildizgames.module.network.netty.client.WebSocketClientNetty;
import be.yildizgames.module.network.netty.server.ServerNetty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

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
    static Client createClientNetty() {
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
    static Client createSimpleClientNetty() {
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
        return ServerNetty.fromPort(factory, port);
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
        return ServerNetty.fromAddress(factory, address, port);
    }
}
