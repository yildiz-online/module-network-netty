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

package be.yildiz.module.network.netty.server;

import be.yildiz.module.network.AbstractHandler;
import be.yildiz.module.network.server.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Optional;

/**
 * Base handler for netty messages, just manage reception of messages.
 *
 * @author Grégory Van den Borre
 */
public final class AnonymousMessageHandler extends SimpleChannelInboundHandler<String> {

    /**
     * Will handle every message received.
     */
    private final AbstractHandler handler;

    /**
     * Session established once the first message is received.
     */
    private Optional<Session> session = Optional.empty();

    /**
     * Create a new message handler.
     *
     * @param handler Handler to use every time a message is received.
     */
    public AnonymousMessageHandler(final AbstractHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public final void channelRead0(final ChannelHandlerContext ctx, final String message) throws Exception {
        if (!session.isPresent()) {
            this.session = Optional.of(NettySession.createAnonymous(ctx.channel()));
        }
        this.handler.processMessages(this.session.get(), message);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable e) throws Exception {
        this.session.ifPresent(Session::disconnect);
    }

}
