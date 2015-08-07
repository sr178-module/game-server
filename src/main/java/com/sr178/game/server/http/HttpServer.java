package com.sr178.game.server.http;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.concurrent.PaddedAtomicBoolean;
import com.sr178.game.server.manager.IServer;


public class HttpServer implements IServer,BeanPostProcessor{

    private final PaddedAtomicBoolean started;

    private HandlerObject[] handlers;

    private final ServerBootstrap bootStrap;
    private final ChannelGroup allChannels;
    
    private List<IHttpHandler>  httpHandler = new ArrayList<IHttpHandler>();
    
    private IHttpPreHandler preHandler;
    
    private int port;
    
    private int workerCount;

    public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getWorkerCount() {
		return workerCount;
	}

	public void setWorkerCount(int workerCount) {
		this.workerCount = workerCount;
	}

	public HttpServer(){
        this.started = new PaddedAtomicBoolean(false);

        this.allChannels = new DefaultChannelGroup();
        this.handlers = new HandlerObject[0];

        bootStrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool(), 8));

        bootStrap.setOption("child.tcpNoDelay", true);
        bootStrap.setOption("child.keepAlive", true);
        bootStrap.setOption("child.sendBufferSize", 1024);
        bootStrap.setOption("child.receiveBufferSize", 1024);

        bootStrap.setPipelineFactory(new ChannelPipelineFactory(){

            @Override
            public ChannelPipeline getPipeline() throws Exception{
                return Channels.pipeline(new HttpRequestDecoder(),
                        new HttpResponseEncoder(), new HttpHandler());
            }
        });
    }

//    public HttpServer(int workerCount){
//        this.started = new PaddedAtomicBoolean(false);
//
//        this.allChannels = new DefaultChannelGroup();
//        this.handlers = new HandlerObject[0];
//
//        bootStrap = new ServerBootstrap(new NioServerSocketChannelFactory(
//                Executors.newCachedThreadPool(),
//                Executors.newCachedThreadPool(), workerCount));
//
//        bootStrap.setOption("child.tcpNoDelay", true);
//        bootStrap.setOption("child.keepAlive", true);
//        bootStrap.setOption("child.sendBufferSize", 1024);
//        bootStrap.setOption("child.receiveBufferSize", 1024);
//
//        bootStrap.setPipelineFactory(new ChannelPipelineFactory(){
//
//            @Override
//            public ChannelPipeline getPipeline() throws Exception{
//                return Channels.pipeline(new HttpRequestDecoder(),
//                        new HttpResponseEncoder(), new HttpHandler());
//            }
//        });
//    }

    public void register(String path, IHttpHandler handler){
        checkArgument(!started.get(), "");

        String actualPath = path.trim();

        if (path.length() == 0 || path.charAt(0) != '/'){
            throw new IllegalArgumentException("");
        }

        LogSystem.info("registering "+ actualPath);

        HandlerObject[] oldObjects = handlers;
        // 
        for (HandlerObject o : oldObjects){
            checkArgument(!o.path.equals(path), "HttpServer.register",
                    path);
        }

        HandlerObject[] newObjects = Arrays.copyOf(oldObjects,
                oldObjects.length + 1);
        newObjects[oldObjects.length] = new HandlerObject(path, handler);
        Arrays.sort(newObjects);

        handlers = newObjects;
    }

    public void start(int port){
        boolean ok = started.compareAndSet(false, true);
        checkArgument(ok, "HttpServer start");

        allChannels.add(bootStrap.bind(new InetSocketAddress(port)));
        LogSystem.info("HttpServer serving at "+port+",workerCount="+workerCount);
    }

    public void start(String address, int port){
        boolean ok = started.compareAndSet(false, true);
        checkArgument(ok, "HttpServer服务器start");
        allChannels.add(bootStrap.bind(new InetSocketAddress(address, port)));
        LogSystem.info("HttpServer serving at  "+address+":"+port+",workerCount="+workerCount);
    }

    public void close(){
        boolean ok = started.compareAndSet(true, false);
        checkArgument(ok, "HttpServer关闭close");
        LogSystem.debug("开始关闭HttpServer");
        allChannels.close().awaitUninterruptibly();
        bootStrap.releaseExternalResources();
        LogSystem.info("http服务器关闭~~");
    }

    private static final ChannelBuffer CHANNEL_BUFFER_404 = HttpUtil
            .codeOnly(HttpResponseStatus.NOT_FOUND);

    private static class HandlerObject implements Comparable<HandlerObject>{

        public final String path;

        public final IHttpHandler handler;

        public final int pathLen;

        HandlerObject(String path, IHttpHandler handler){
            super();
            this.path = path;
            this.handler = handler;
            this.pathLen = path.length();
        }

        private boolean isMatch(String uri){
            return uri.startsWith(path);
        }

        private String stripPrefix(String uri){
            return uri.substring(pathLen);
        }

        @Override
        public int compareTo(HandlerObject o){
            int len1 = path.length();
            int len2 = o.path.length();

            if (len1 < len2){
                return 1;
            }

            if (len1 > len2){
                return -1;
            }

            return path.compareTo(o.path);
        }

    }

    private class HttpHandler extends SimpleChannelUpstreamHandler{

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
                throws Exception{
        	LogSystem.debug("收到访问请求前奏······················ ");
            if (e instanceof MessageEvent
                    && ((MessageEvent) e).getMessage() instanceof HttpRequest){
                HttpRequest request = (HttpRequest) ((MessageEvent) e)
                        .getMessage();
//                boolean isKeepAlive = HttpHeaders.isKeepAlive(request);
                String uri = request.getUri();
                LogSystem.debug("收到访问请求--: "+uri);
                // handler
                for (HandlerObject o : handlers){
                    if (o.isMatch(uri)){
                    	try{
                    		String requestUrl = "";
                    		if(request.getMethod().equals(HttpMethod.GET)){
                    			requestUrl = o.stripPrefix(uri);
                    		}else{
                    			requestUrl =  "?"+new String(request.getContent().array());
                    		}
                    		Request requestParse = RequestParser.parse(requestUrl);
                    		//拦截器判断
                    		if(preHandler!=null&&uri.startsWith(preHandler.getInterceptorUrl())){
                    			preHandler.preHandler(requestParse, ctx.getChannel());
                    		}
                    		//执行具体业务
                            Object result = o.handler.handle(requestParse,
                                    ctx.getChannel());
                            if(result!=null){
                            	HttpUtil.renderSuccess(ctx.getChannel(), result);
                            }
                    	}catch(Throwable e1){
                    		LogSystem.error(e1, "");
                    		HttpUtil.renderError(ctx.getChannel());
                    	}
                        return;
                    }
                }
                LogSystem.warn("不存在handler: "+ uri);
                ctx.getChannel().write(
                        CHANNEL_BUFFER_404).addListener(ChannelFutureListener.CLOSE);
            }else{
            	LogSystem.warn("http请求没有被处理············");
            }
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e){
            if (e.getCause() instanceof java.io.IOException){
            } else{
                e.getCause().printStackTrace();
            }

            e.getChannel().close();
        }
        @Override
        public void channelConnected(
                ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            LogSystem.debug("http,新建连接"+ctx.getChannel().getId());
        }
        @Override
        public void channelClosed(
                ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        	LogSystem.debug("http,断开连接"+ctx.getChannel().getId());
        }
    }
	@Override
	public void start() {
		 for(IHttpHandler rhandler:httpHandler){
			 this.register(rhandler.getMappingUrl(), rhandler);
		 }
		this.start(port);
	}

	@Override
	public void restart() {
		shutdown();
		start();
		
	}
	@Override
	public void shutdown() {
		 this.close();
	}
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(bean instanceof IHttpHandler){
			httpHandler.add((IHttpHandler)bean);
		}
		if(bean instanceof IHttpPreHandler){
			preHandler = (IHttpPreHandler)bean;
		}
		return bean;
	}
}
