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

import be.yildizgames.module.network.AbstractHandler;
import be.yildizgames.module.network.server.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Optional;

/**
 * @author Grégory Van den Borre
 */
public abstract class AbstractSessionMessageHandler<T> extends SimpleChannelInboundHandler<T> {

    protected final AbstractHandler handler;

    private Session session;

    private static final System.Logger LOGGER = System.getLogger(AbstractSessionMessageHandler.class.getName());

    protected AbstractSessionMessageHandler(final AbstractHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public final void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        LOGGER.log(System.Logger.Level.ERROR, e);
        this.getSession().ifPresent(Session::disconnect);
    }

    /**
     * @return The associated session.
     */
    protected final Optional<Session> getSession() {
        return Optional.ofNullable(this.session);
    }

    protected final void setSession(Session session) {
        this.session = session;
    }
}
