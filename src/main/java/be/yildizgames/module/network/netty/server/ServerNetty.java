/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2019 Grégory Van den Borre
 *
 *  More infos available: https://engine.yildiz-games.be
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

package be.yildizgames.module.network.netty.server;

import be.yildizgames.module.network.DecoderEncoder;
import be.yildizgames.module.network.netty.NettyChannelInitializer;
import be.yildizgames.module.network.server.Server;
import be.yildizgames.module.network.server.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Server side part of the Netty network system, wrap the netty bootstrap and offer possibility to add handlers.
 *
 * @author Grégory Van den Borre
 */
public final class ServerNetty extends Server {

    private static final System.Logger LOGGER = System.getLogger(ServerNetty.class.getName());

    /**
     * Netty server bootstrap.
     */
    private final ServerBootstrap bootstrap;

    /**
     * Create a new Netty server.
     */
    private ServerNetty() {
        super();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class);
    }

    /**
     * Create a new Netty server.
     *
     * @return The created server.
     */
    //@requires bootstrap != null.
    //@requires address != null.
    //@requires port > 0 < 65535.
    public static ServerNetty create() {
        return new ServerNetty();
    }


    /**
     * Start the server to listen to clients.
     */
    //@ensures To start the server and having the port listening to clients.
    @Override
    public void startServer(int port, SessionManager sessionManager, DecoderEncoder codec) {
        this.startServer(null, port, sessionManager, codec);
    }


    /**
     * Start the server to listen to clients.
     */
    //@ensures To start the server and having the port listening to clients.
    @Override
    public void startServer(String address, int port, SessionManager sessionManager, DecoderEncoder codec) {
        try {
            ChannelInitializer<SocketChannel> initializer = new NettyChannelInitializer(new SessionServerHandlerFactory(sessionManager, codec));

            this.bootstrap.childHandler(initializer);
            InetSocketAddress socketAddress;
            if (address == null) {
                socketAddress = new InetSocketAddress(port);
            } else {
                socketAddress = new InetSocketAddress(address, port);
            }
            ChannelFuture acceptor = this.bootstrap.bind(socketAddress).sync();
            if (acceptor.isSuccess()) {
                LOGGER.log(System.Logger.Level.INFO,"Network server bound to " + port);
            } else {
                LOGGER.log(System.Logger.Level.WARNING,"Network server binding to {} failure.", port);
            }
        } catch (ChannelException e) {
            this.throwError("Port " + port + " already in use.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.throwError("Error starting network engine.", e);
        }
    }
}
