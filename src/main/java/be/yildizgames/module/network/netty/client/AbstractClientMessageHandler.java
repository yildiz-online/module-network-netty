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

package be.yildizgames.module.network.netty.client;

import be.yildizgames.module.network.client.ClientCallBack;
import be.yildizgames.module.network.protocol.MessageSeparation;
import be.yildizgames.module.network.protocol.MessageWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grégory Van den Borre
 */
public abstract class AbstractClientMessageHandler <T> extends SimpleChannelInboundHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientMessageHandler.class);

    private static final int BUFFER_SIZE = 1024;
    /**
     * Used to store an incomplete message waiting for its other parts.
     */
    private final List<String> cutMessage = new ArrayList<>();

    private final ClientCallBack callBack;

    AbstractClientMessageHandler(ClientCallBack cb) {
        super();
        this.callBack = cb;
    }

    @Override
    public final void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.callBack.connectionFailed();
        LOGGER.info("Netty channel closed: {}", ctx.channel());
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable e) {
        LOGGER.error("Exception", e);
        ctx.channel().close();
        callBack.connectionLost();
    }

    void handleMessage(String message) {
        if (message.endsWith(MessageSeparation.MESSAGE_END)) {
            if (!this.cutMessage.isEmpty()) {
                StringBuilder sb = new StringBuilder(BUFFER_SIZE);
                this.cutMessage.forEach(sb::append);
                sb.append(message);
                this.processMessage(sb.toString());

                this.cutMessage.clear();
            } else {
                this.processMessage(message);
            }
        } else {
            this.cutMessage.add(message);
        }
    }

    /**
     * Process the message to cut it in readable message.
     *
     * @param message Message to process.
     */
    private void processMessage(final String message) {
        String messageWithoutStartChar = message.replaceAll(MessageSeparation.MESSAGE_BEGIN, "");
        final String[] messages = messageWithoutStartChar.split(MessageSeparation.MESSAGE_END);
        for (final String c : messages) {
            MessageWrapper current = new MessageWrapper(c);
            callBack.messageReceived(current);
        }
    }
}
