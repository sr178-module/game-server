package com.sr178.game.server.socket.mina.codefactory;


import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CodeFactory implements ProtocolCodecFactory {
    private final ProtocolEncoder encoder;

    private final ProtocolDecoder decoder;

    public static final int HEAD_LENGTH = 5;
    
    public CodeFactory() {
        encoder = new Encoder();
        decoder = new Decoder();
    }
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}
}
