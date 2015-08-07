package com.sr178.game.server.http;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.handler.codec.http.HttpConstants.COLON;
import static org.jboss.netty.handler.codec.http.HttpConstants.CR;
import static org.jboss.netty.handler.codec.http.HttpConstants.LF;
import static org.jboss.netty.handler.codec.http.HttpConstants.SP;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.TEMPORARY_REDIRECT;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;

import com.alibaba.fastjson.JSON;
import com.sr178.game.server.http.util.Empty;
import com.sr178.game.server.http.util.StringEncoder;


public class HttpUtil {
	 public static ChannelBuffer encodeResponse(HttpResponse m){
	        boolean contentMustBeEmpty;
	        if (m.isChunked()){
	            // if Content-Length is set then the message can't be HTTP chunked
	            if (isContentLengthSet(m)){
	                contentMustBeEmpty = false;
	                removeTransferEncodingChunked(m);
	            } else{
	                // check if the Transfer-Encoding is set to chunked already.
	                // if not add the header to the message
	                if (!isTransferEncodingChunked(m)){
	                    m.headers().add(Names.TRANSFER_ENCODING, Values.CHUNKED);
	                }
	                contentMustBeEmpty = true;
	            }
	        } else{
	            contentMustBeEmpty = isTransferEncodingChunked(m);
	        }

	        ChannelBuffer header = dynamicBuffer();
	        encodeInitialLine(header, m);
	        encodeHeaders(header, m);
	        header.writeByte(CR);
	        header.writeByte(LF);

	        ChannelBuffer content = m.getContent();
	        if (!content.readable()){
	            return header; // no content
	        } else if (contentMustBeEmpty){
	            throw new IllegalArgumentException(
	                    "HttpMessage.content must be empty "
	                            + "if Transfer-Encoding is chunked.");
	        } else{
	            return wrappedBuffer(header, content);
	        }
	    }

	    public static void renderSuccess(Channel channel,Object content){
            String str =  JSON.toJSONString(content);
            HttpMessage message = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            byte[] contentByte = str.getBytes(); 
	        HttpHeaders.addHeader(message, HttpHeaders.Names.CONTENT_TYPE,
	                "text/html; charset=utf-8");
	        HttpHeaders.setHeader(message, HttpHeaders.Names.CONTENT_LENGTH,
	        		contentByte.length);
    		message.setContent(ChannelBuffers.wrappedBuffer(contentByte));
    		channel.write(message).addListener(ChannelFutureListener.CLOSE);
	    }
	    
	    public static void renderError(Channel channel){
            HttpMessage message = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
	        HttpHeaders.addHeader(message, HttpHeaders.Names.CONTENT_TYPE,
	                "text/html; charset=utf-8");
	        HttpHeaders.setHeader(message, HttpHeaders.Names.CONTENT_LENGTH,
	        		0);
	    	channel.write(message).addListener(ChannelFutureListener.CLOSE);
	    }
	    
	    public static ChannelBuffer encodeResponse(HttpResponseStatus status,
	            byte[] content){
	        DefaultHttpResponse response = new DefaultHttpResponse(
	                HttpVersion.HTTP_1_1, status);
	        HttpHeaders.addHeader(response, HttpHeaders.Names.CONTENT_TYPE,
	                "text/html; charset=utf-8");

	        ChannelBuffer buffer = new BigEndianHeapChannelBuffer(content);
	        response.setContent(buffer);
	        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_LENGTH,
	                content.length);
	        return encodeResponse(response);
	    }

	    public static final HttpResponse redirectTemporarily(String url){
	        HttpResponse response = new DefaultHttpResponse(HTTP_1_1,
	                TEMPORARY_REDIRECT);
	        HttpHeaders.setHeader(response, HttpHeaders.Names.LOCATION, url);
	        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_LENGTH, 0);
	        return response;
	    }

	    public static final HttpResponse redirectPermanently(String url){
	        HttpResponse response = new DefaultHttpResponse(HTTP_1_1,
	                HttpResponseStatus.MOVED_PERMANENTLY);
	        HttpHeaders.setHeader(response, HttpHeaders.Names.LOCATION, url);
	        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_LENGTH, 0);
	        return response;
	    }

	    public static ChannelBuffer encodeResponse(HttpResponseStatus status,
	            String content){
	        return encodeResponse(status, StringEncoder.encode(content));
	    }

	    public static ChannelBuffer codeOnly(HttpResponseStatus status){
	        return encodeResponse(status, Empty.BYTE_ARRAY);
	    }

	    private static void encodeHeaders(ChannelBuffer buf, HttpResponse message){
	        try{
	            for (Map.Entry<String, String> h : message.headers()){
	                encodeHeader(buf, h.getKey(), h.getValue());
	            }
	        } catch (UnsupportedEncodingException e){
	            throw (Error) new Error().initCause(e);
	        }
	    }

	    private static void encodeHeader(ChannelBuffer buf, String header,
	            String value) throws UnsupportedEncodingException{
	        encodeAscii(header, buf);
	        buf.writeByte(COLON);
	        buf.writeByte(SP);
	        encodeAscii(value, buf);
	        buf.writeByte(CR);
	        buf.writeByte(LF);
	    }

	    private static void encodeInitialLine(ChannelBuffer buf,
	            HttpResponse response){
	        encodeAscii(response.getProtocolVersion().toString(), buf);
	        buf.writeByte(SP);
	        encodeAscii(String.valueOf(response.getStatus().getCode()), buf);
	        buf.writeByte(SP);
	        encodeAscii(String.valueOf(response.getStatus().getReasonPhrase()), buf);
	        buf.writeByte(CR);
	        buf.writeByte(LF);
	    }

	    private static void encodeAscii(String s, ChannelBuffer buf){
	        for (int i = 0; i < s.length(); i++){
	            buf.writeByte(s.charAt(i));
	        }
	    }

//	    private static void validateHeaderName(String name){
//	        if (name == null){
//	            throw new NullPointerException("name");
//	        }
//	        for (int i = 0; i < name.length(); i++){
//	            char c = name.charAt(i);
//	            if (c > 127){
//	                throw new IllegalArgumentException(
//	                        "name contains non-ascii character: " + name);
//	            }
//
//	            // Check prohibited characters.
//	            switch (c){
//	                case '\t':
//	                case '\n':
//	                case 0x0b:
//	                case '\f':
//	                case '\r':
//	                case ' ':
//	                case ',':
//	                case ':':
//	                case ';':
//	                case '=':
//	                    throw new IllegalArgumentException(
//	                            "name contains one of the following prohibited characters: "
//	                                    + "=,;: \\t\\r\\n\\v\\f: " + name);
//	            }
//	        }
//	    }

//	    private static void validateHeaderValue(String value){
//	        if (value == null){
//	            throw new NullPointerException("value");
//	        }
//
//	        // 0 - the previous character was neither CR nor LF
//	        // 1 - the previous character was CR
//	        // 2 - the previous character was LF
//	        int state = 0;
//
//	        for (int i = 0; i < value.length(); i++){
//	            char c = value.charAt(i);
//
//	            // Check the absolutely prohibited characters.
//	            switch (c){
//	                case 0x0b: // Vertical tab
//	                    throw new IllegalArgumentException(
//	                            "value contains a prohibited character '\\v': "
//	                                    + value);
//	                case '\f':
//	                    throw new IllegalArgumentException(
//	                            "value contains a prohibited character '\\f': "
//	                                    + value);
//	            }
//
//	            // Check the CRLF (HT | SP) pattern
//	            switch (state){
//	                case 0:
//	                    switch (c){
//	                        case '\r':
//	                            state = 1;
//	                            break;
//	                        case '\n':
//	                            state = 2;
//	                            break;
//	                    }
//	                    break;
//	                case 1:
//	                    switch (c){
//	                        case '\n':
//	                            state = 2;
//	                            break;
//	                        default:
//	                            throw new IllegalArgumentException(
//	                                    "Only '\\n' is allowed after '\\r': "
//	                                            + value);
//	                    }
//	                    break;
//	                case 2:
//	                    switch (c){
//	                        case '\t':
//	                        case ' ':
//	                            state = 0;
//	                            break;
//	                        default:
//	                            throw new IllegalArgumentException(
//	                                    "Only ' ' and '\\t' are allowed after '\\n': "
//	                                            + value);
//	                    }
//	            }
//	        }
//
//	        if (state != 0){
//	            throw new IllegalArgumentException(
//	                    "value must not end with '\\r' or '\\n':" + value);
//	        }
//	    }

	    private static boolean isTransferEncodingChunked(HttpMessage m){
	        List<String> chunked = m.headers().getAll(
	                HttpHeaders.Names.TRANSFER_ENCODING);
	        if (chunked.isEmpty()){
	            return false;
	        }

	        for (String v : chunked){
	            if (v.equalsIgnoreCase(HttpHeaders.Values.CHUNKED)){
	                return true;
	            }
	        }
	        return false;
	    }

	    private static void removeTransferEncodingChunked(HttpMessage m){
	        List<String> values = m.headers().getAll(
	                HttpHeaders.Names.TRANSFER_ENCODING);
	        if (values.isEmpty()){
	            return;
	        }
	        Iterator<String> valuesIt = values.iterator();
	        while (valuesIt.hasNext()){
	            String value = valuesIt.next();
	            if (value.equalsIgnoreCase(HttpHeaders.Values.CHUNKED)){
	                valuesIt.remove();
	            }
	        }
	        if (values.isEmpty()){
	            m.headers().remove(HttpHeaders.Names.TRANSFER_ENCODING);
	        } else{
	            m.headers().set(HttpHeaders.Names.TRANSFER_ENCODING, values);
	        }
	    }

	    private static boolean isContentLengthSet(HttpMessage m){
	        List<String> contentLength = m.headers().getAll(
	                HttpHeaders.Names.CONTENT_LENGTH);
	        return !contentLength.isEmpty();
	    }

	    public static String encodeURL(String data){
	        try{
	            return URLEncoder.encode(data, "UTF-8");
	        } catch (UnsupportedEncodingException e){
	            throw new RuntimeException(e);
	        }
	    }
}
