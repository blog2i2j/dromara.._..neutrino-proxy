package fun.asgc.neutrino.proxy.server.proxy.handler;

import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
import fun.asgc.neutrino.proxy.server.util.ProxyUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.noear.solon.annotation.Component;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Match(type = Constants.ProxyDataTypeName.DISCONNECT)
@Component
public class ProxyMessageDisconnectHandler implements ProxyMessageHandler {

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		Integer licenseId = ctx.channel().attr(Constants.LICENSE_ID).get();
		// licenseId为空，说明访问者通道已经关闭，无需处理
		if (null == licenseId) {
			return;
		}
		// 代理连接没有连上服务器由控制连接发送用户端断开连接消息
		String visitorId = proxyMessage.getInfo();
		Channel userChannel = ProxyUtil.removeVisitorChannelFromCmdChannel(ctx.channel(), visitorId);
		if (null != userChannel) {
			// 数据发送完成后再关闭连接，解决http1.0数据传输问题
			userChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.DISCONNECT.getDesc();
	}

}
