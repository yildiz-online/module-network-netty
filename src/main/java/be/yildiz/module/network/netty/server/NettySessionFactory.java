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

import be.yildiz.common.id.PlayerId;
import be.yildiz.module.network.netty.DecoderEncoder;
import be.yildiz.module.network.server.Session;
import io.netty.channel.Channel;

/**
 * @author Grégory Van den Borre
 */
public class NettySessionFactory {

    /**
     * Create a new session associated to a player.
     *
     * @param player  Id of the logged player.
     * @param channel Associated Netty channel.
     * @return The created session.
     */
    static Session createText(final PlayerId player, final Channel channel, DecoderEncoder codec) {
        return new TextNettySession(player, channel);
    }

    /**
     * Create a new session with no associated player and set it setAuthenticated.
     *
     * @param channel Associated Netty channel.
     * @return The created session.
     */
    static Session createAnonymousText(final Channel channel) {
        return new TextNettySession(PlayerId.WORLD, channel);
    }

    /**
     * Create a new session associated to a player.
     *
     * @param player  Id of the logged player.
     * @param channel Associated Netty channel.
     * @return The created session.
     */
    static Session createWebSocket(final PlayerId player, final Channel channel) {
        return new WebSocketNettySession(player, channel);
    }

    /**
     * Create a new session with no associated player and set it setAuthenticated.
     *
     * @param channel Associated Netty channel.
     * @return The created session.
     */
    static Session createAnonymousWebSocket(final Channel channel) {
        return new WebSocketNettySession(PlayerId.WORLD, channel);
    }
}
