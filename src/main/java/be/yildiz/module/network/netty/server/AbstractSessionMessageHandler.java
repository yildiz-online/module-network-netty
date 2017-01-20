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

import be.yildiz.module.network.AbstractHandler;
import be.yildiz.module.network.server.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Optional;

/**
 * @author Grégory Van den Borre
 */
public abstract class AbstractSessionMessageHandler<T> extends SimpleChannelInboundHandler<T> {

    protected final AbstractHandler handler;

    private Session session;

    public AbstractSessionMessageHandler(final AbstractHandler handler) {
        super();
        this.handler = handler;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        this.getSession().ifPresent(Session::disconnect);
    }

    /**
     * @return The associated session.
     */
    protected final Optional<Session> getSession() {
        return Optional.ofNullable(this.session);
    }

    protected void setSession(Session session) {
        this.session = session;
    }
}