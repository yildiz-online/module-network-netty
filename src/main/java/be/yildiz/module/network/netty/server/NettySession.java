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

import be.yildiz.common.id.PlayerId;
import be.yildiz.module.network.server.Session;
import io.netty.channel.Channel;

/**
 * Netty implementation for a session.
 *
 * @author Grégory Van den Borre
 */
abstract class NettySession extends Session {

    /**
     * Channel used for data transmission.
     */
    private final Channel channel;

    /**
     * Full constructor.
     *
     * @param player  Id of the logged player.
     * @param channel Associated Netty channel.
     */
    protected NettySession(final PlayerId player, final Channel channel) {
        super(player);
        this.channel = channel;
    }

    @Override
    public  final void sendMessage(String message) {
        this.write(this.channel, message);
    }

    protected abstract void write(final Channel ch, final String message);

    @Override
    protected void closeSession() {
        this.channel.close();
    }

    public Channel getChannel() {
        return channel;
    }
}
