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

package be.yildizgames.module.network.netty.server;

import be.yildizgames.common.logging.LogFactory;
import be.yildizgames.module.network.DecoderEncoder;
import be.yildizgames.module.network.netty.HandlerFactory;
import be.yildizgames.module.network.netty.NettyChannelInitializer;
import be.yildizgames.module.network.server.Server;
import be.yildizgames.module.network.server.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Server side part of the Netty network system, wrap the netty bootstrap and offer possibility to add handlers.
 *
 * @author Grégory Van den Borre
 */
public final class ServerNetty extends Server {

    private static final Logger LOGGER = LogFactory.getInstance().getLogger(ServerNetty.class);

    /**
     * Port to expose to clients.
     */
    private final int port;

    /**
     * Address to expose to clients.
     */
    private final String address;

    /**
     * Netty server bootstrap.
     */
    private final ServerBootstrap bootstrap;

    /**
     * Create a new Netty server.
     *
     * @param address   Address to expose to clients.
     * @param port      Port to expose to clients.
     */
    //@requires bootstrap != null.
    //@requires port > 0 < 65535.
    private ServerNetty(final String address, final int port, SessionManager sessionManager, DecoderEncoder codec) {
        super();
        ChannelInitializer<SocketChannel> initializer = new NettyChannelInitializer(new SessionServerHandlerFactory(sessionManager, codec));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(initializer);
        this.address = address;
        this.port = port;
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    /**
     * Create a new Netty server.
     *
     * @param address   Address to expose to clients.
     * @param port      Port to expose to clients.
     * @return The created server.
     */
    //@requires bootstrap != null.
    //@requires address != null.
    //@requires port > 0 < 65535.
    public static ServerNetty webSocket(final String address, final int port, SessionManager sessionManager) {
        return new ServerNetty(faddress, port, sessionManager, DecoderEncoder.WEBSOCKET);
    }

    /**
     * Create a new Netty server.
     *
     * @param port      Port to expose to clients.
     * @return The created server.
     */
    // @requires bootstrap != null.
    //@requires port > 0 < 65535.
    //@effects Create a new instance of the Netty server.
    public static ServerNetty fromPort(final HandlerFactory factory, final int port) {
        return new ServerNetty(factory,null, port);
    }


    /**
     * Start the server to listen to clients.
     */
    //@ensures To start the server and having the port listening to clients.
    public void startServer() {
        try {
            InetSocketAddress socketAddress;
            if (this.address == null) {
                socketAddress = new InetSocketAddress(this.port);
            } else {
                socketAddress = new InetSocketAddress(this.address, this.port);
            }
            ChannelFuture acceptor = this.bootstrap.bind(socketAddress).sync();
            if (acceptor.isSuccess()) {
                LOGGER.debug("server bound to :" + this.port);
                LOGGER.info("Server started.");
            } else {
                LOGGER.warn("server not bound to :" + this.port);
                LOGGER.info("Server not started.");
            }
        } catch (ChannelException e) {
            this.throwError("Port " + this.port + " already in use.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.throwError("Error starting network engine.", e);
        }
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }
}
