package com.sipgate.siptest.sip;

import com.sipgate.siptest.SipgateUserAgent;
import com.sipgate.type.extension.E;
import java.nio.file.Path;
import net.sourceforge.peers.sip.core.useragent.SipListener;

public class SipgateUserAgentBuilder {

	private E register;
	private String secret;
	private SipListener listener;
	private String name;
	private Path audioOutput;

	public SipgateUserAgentBuilder forExtension(E register) {
		this.register = register;
		return this;
	}

	public SipgateUserAgentBuilder withSecret(String secret) {
		this.secret = secret;
		return this;
	}

	public SipgateUserAgentBuilder withListener(SipListener listener) {
		this.listener = listener;
		return this;
	}

	public SipgateUserAgentBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public SipgateUserAgentBuilder withAudioOutput(Path audioOutput) {
		this.audioOutput = audioOutput;
		return this;
	}

	public SipgateUserAgent build() {
		return new SipgateUserAgent(register, secret, listener, name, audioOutput);
	}

}
