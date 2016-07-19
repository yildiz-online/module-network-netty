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

package be.yildiz.module.network.netty;

import be.yildiz.common.collections.Lists;
import be.yildiz.common.exeption.UnhandledSwitchCaseException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Pipeline factory to build a pipeline to use for netty transfer, every time a channel is initialized, the pipeline is
 * filled with the handler provided by this class.
 *
 * @author Grégory Van den Borre
 */
public final class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * List of handler to register every time a channel is initialized.
     */
    private final List<ChannelHandler> handlers = Lists.newList();

    /**
     * Create additional handler every time a channel is initialized.
     */
    private final HandlerFactory factory;

    /**
     * Create a new instance and register the handlers.
     *
     * @param codec   Used to register the handler to add to the channel once initialize.
     * @param factory Create additional handler every time a channel is initialized.
     * @Requires codec != null
     * @Requires factory != null
     */
    public NettyChannelInitializer(final DecoderEncoder codec, final HandlerFactory factory) {
        super();
        this.factory = factory;
        switch (codec) {
            case STRING:
                this.handlers.add(new StringEncoder(CharsetUtil.UTF_8));
                this.handlers.add(new StringDecoder(CharsetUtil.UTF_8));
                break;
            case HTTP:
                this.handlers.add(new HttpServerCodec());
                this.handlers.add(new HttpObjectAggregator(65536));
                this.handlers.add(new ChunkedWriteHandler());
                break;
            default:
                throw new UnhandledSwitchCaseException(codec);
        }
    }


    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        this.handlers.forEach(pipeline::addLast);
        pipeline.addLast("handler", this.factory.create());
    }


    /**
     * Possible codec between client and server.
     *
     * @author Van den Borre Grégory
     */
    public enum DecoderEncoder {

        /**
         * Simple string messages, using UTF-8.
         */
        STRING,

        /**
         * Transfer for files using HTTP protocol.
         */
        HTTP,

        /**
         * Transfer using ZLIB compression.
         */
        ZLIB;
    }


}
