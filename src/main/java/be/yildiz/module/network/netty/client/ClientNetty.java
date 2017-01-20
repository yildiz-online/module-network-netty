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

package be.yildiz.module.network.netty.client;

import be.yildiz.common.log.Logger;
import be.yildiz.module.network.client.AbstractNetworkEngineClient;
import be.yildiz.module.network.netty.DecoderEncoder;
import be.yildiz.module.network.protocol.ServerRequest;
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
public abstract class ClientNetty<T> extends AbstractNetworkEngineClient {

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
    public ClientNetty(final Bootstrap clientBootstrap) {
        super();
        Logger.info("Initializing Netty network client engine...");
        this.bootstrap = clientBootstrap;
        Logger.info("Netty network engine client initialized.");

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
    public void connect(final String address, final int port) {
        Logger.info("Connecting to server " + address + " : " + port);

        ChannelFuture future = this.bootstrap.connect(new InetSocketAddress(address, port));
        if (!future.awaitUninterruptibly().isSuccess()) {
            this.connectionFailed();
            this.bootstrap.group().shutdownGracefully();
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
        this.bootstrap.group().shutdownGracefully();
    }

    @Override
    public void sendMessage(final ServerRequest message) {
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
