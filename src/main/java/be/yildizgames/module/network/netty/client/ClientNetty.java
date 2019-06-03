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

package be.yildizgames.module.network.netty.client;

import be.yildizgames.module.network.DecoderEncoder;
import be.yildizgames.module.network.client.Client;
import be.yildizgames.module.network.protocol.NetworkMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Netty implementation for a client.
 *
 * @author Grégory Van den Borre
 */
public abstract class ClientNetty<T> extends Client {

    private static final System.Logger LOGGER = System.getLogger(ClientNetty.class.getName());

    /**
     * Netty bootstrap object.
     */
    private final Bootstrap bootstrap;

    /**
     * Connection to the server.
     */
    private Channel channel;

    /**
     * Create a new instance of a client.
     *
     * @param clientBootstrap Netty bootstrap object.
     */
    ClientNetty(final Bootstrap clientBootstrap) {
        super();
        LOGGER.log(System.Logger.Level.INFO,"Initializing Netty network client engine...");
        this.bootstrap = clientBootstrap;
        LOGGER.log(System.Logger.Level.INFO,"Netty network engine client initialized.");

    }

    /**
     * Set the time out, in milliseconds.
     *
     * @param timeout Timeout value.
     */
    public void setTimeOut(final int timeout) {
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout);
    }

    @Override
    public void connectImpl(final String address, final int port) {
        LOGGER.log(System.Logger.Level.INFO, "Connecting to server {}:{}", address, port);
        ChannelFuture future = this.bootstrap.connect(new InetSocketAddress(address, port));
        if (!future.awaitUninterruptibly().isSuccess()) {
            this.connectionFailed();
            this.bootstrap.config().group().shutdownGracefully();
        } else {
            this.channel = future.channel();
            this.connectionComplete();
        }
    }

    protected abstract void connectionComplete();

    @Override
    public void close() {
        Optional.ofNullable(this.channel)
                .ifPresent(c -> {
                    c.disconnect();
                    c.close();
                    this.connectionLost();
                });
        this.channel = null;
        this.bootstrap.config().group().shutdownGracefully();
    }

    @Override
    public void sendMessage(final NetworkMessage message) {
        this.sendMessage(message.buildMessage());
    }

    @Override
    public void sendMessage(final String message) {
        Optional.ofNullable(this.channel)
                .ifPresent(c -> c.writeAndFlush(this.buildMessage(message)));
    }

    @Override
    public void disconnect() {
        Optional.ofNullable(this.channel)
                .ifPresent(Channel::disconnect);
    }

    protected abstract T buildMessage(String message);

    public abstract  DecoderEncoder getCodec();
}
