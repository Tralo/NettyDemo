package com.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatServerHandler extends SimpleChannelInboundHandler {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]/"+ channel.remoteAddress() + "上线了 " + sdf.format(new Date()));
        channelGroup.add(channel);
        System.out.println(channel.remoteAddress() + " 上线了" + sdf.format(new Date()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.remove(channel);
        channelGroup.writeAndFlush("[客户端]/" + channel.remoteAddress() + "下线了 " + sdf.format(new Date()) + "\n");
        System.out.println(channel.remoteAddress() + " 下线了" + sdf.format(new Date()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final Object msg) throws Exception {
        final Channel channel = ctx.channel();
        channelGroup.forEach(ch -> {
            if(channel != ch){
                ch.writeAndFlush(sdf.format(new Date()) + " [客户端]/" + channel.remoteAddress() + "发送了消息: " + msg);
            } else {
                channel.writeAndFlush(sdf.format(new Date()) +  " 自己发送了消息: " + msg);
            }
        });
        System.out.println(sdf.format(new Date()) + " [客户端]/" + channel.remoteAddress() + "发送了消息: " + msg + " \n");
    }
}
