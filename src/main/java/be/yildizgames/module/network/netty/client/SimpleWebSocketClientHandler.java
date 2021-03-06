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

import be.yildizgames.module.network.client.ClientCallBack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Grégory Van den Borre
 */
class SimpleWebSocketClientHandler extends AbstractClientMessageHandler<Object> {

    private static final System.Logger LOGGER = System.getLogger(SimpleWebSocketClientHandler.class.getName());

    private final ClientCallBack callBack;

    private WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    SimpleWebSocketClientHandler(ClientCallBack cb) {
        super(cb);
        this.callBack = cb;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws URISyntaxException {
        String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
        String uri = "ws://" + host + ":" + port + "/websocket";
        this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                new URI(uri), WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
        this.handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object received) {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            if(!(received instanceof FullHttpResponse)) {
                LOGGER.log(System.Logger.Level.WARNING,"Receiving message before handshake complete: {}", received);
                return;
            }
            FullHttpResponse handshake = FullHttpResponse.class.cast(received);
            LOGGER.log(System.Logger.Level.DEBUG,"Handshake received: {}", handshake);
            handshaker.finishHandshake(ch, handshake);
            LOGGER.log(System.Logger.Level.DEBUG,"Handshake complete.");
            handshakeFuture.setSuccess();
            this.callBack.handShakeComplete();
            return;
        }

        if (received instanceof TextWebSocketFrame) {
            String message = TextWebSocketFrame.class.cast(received).text();
            LOGGER.log(System.Logger.Level.DEBUG,"Textframe received: {}", message);
            this.handleMessage(message);
        } else if (received instanceof CloseWebSocketFrame) {
            ch.close();
        }
    }

}
