/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 * Copyright (c) 2017 Grégory Van den Borre
 *
 * More infos available: https://www.yildiz-games.be
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 */

package be.yildiz.module.network.netty.server;

import be.yildiz.common.log.Logger;
import be.yildiz.module.network.exceptions.NetworkException;
import be.yildiz.module.network.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Server side part of the Netty network system, wrap the netty bootstrap and offer possibility to add handlers.
 *
 * @author Grégory Van den Borre
 */
public final class ServerNetty implements Server {

    /**
     * Port to expose to clients.
     */
    @Getter
    private final int port;

    /**
     * Address to expose to clients.
     */
    @Getter
    private final String address;

    /**
     * Netty server bootstrap.
     */
    private final ServerBootstrap bootstrap;

    /**
     * Create a new Netty server.
     *
     * @param bootstrap Netty server bootstrap to use.
     * @param address   Address to expose to clients.
     * @param port      Port to expose to clients.
     */
    //@requires bootstrap != null.
    //@requires port > 0 < 65535.
    private ServerNetty(final ServerBootstrap bootstrap, final String address, final int port) {
        super();
        this.address = address;
        this.port = port;
        this.bootstrap = bootstrap;
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    /**
     * Create a new Netty server.
     *
     * @param bootstrap Netty server bootstrap to use.
     * @param address   Address to expose to clients.
     * @param port      Port to expose to clients.
     */
    //@requires bootstrap != null.
    //@requires address != null.
    //@requires port > 0 < 65535.
    public static ServerNetty fromAddress(final ServerBootstrap bootstrap, final String address, final int port) {
        return new ServerNetty(bootstrap, address, port);
    }

    /**
     * Create a new Netty server.
     *
     * @param bootstrap Netty server bootstrap to use.
     * @param port      Port to expose to clients.
     */
    // @requires bootstrap != null.
    //@requires port > 0 < 65535.
    //@effects Create a new instance of the Netty server.
    public static ServerNetty fromPort(final ServerBootstrap bootstrap, final int port) {
        return new ServerNetty(bootstrap, null, port);
    }


    /**
     * Start the server to listen to clients.
     *
     * @throws NetworkException If the server failed to start(port already used...).
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
                Logger.debug("server bound to :" + this.port);
                Logger.info("Server started.");
            } else {
                Logger.warning("server not bound to :" + this.port);
                Logger.info("Server not started.");
            }
        } catch (ChannelException e) {
            throw new NetworkException("Port " + this.port + " already in use.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Error starting network engine.", e);
        }
    }
}
