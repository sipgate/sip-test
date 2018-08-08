package com.sipgate.siptest;

import static org.slf4j.LoggerFactory.getLogger;

import net.sourceforge.peers.Logger;

public class SipLogger implements Logger {

	private static final org.slf4j.Logger LOGGER = getLogger("com.sipgate.PhoneLogger");
	private static final org.slf4j.Logger TRACE_LOGGER = getLogger("com.sipgate.SipLogger");

	private final String name;

	public SipLogger() {
		this("");
	}

	public SipLogger(String name) {
		this.name = name;
	}

	@Override
	public void debug(String message) {
		LOGGER.debug(personalizeMessage(message));
	}

	@Override
	public void info(String message) {
		LOGGER.info(personalizeMessage(message));
	}

	@Override
	public void error(String message) {
		LOGGER.error(personalizeMessage(message));
	}

	@Override
	public void error(String message, Exception exception) {
		LOGGER.error(personalizeMessage(message), exception);
	}

	@Override
	public void traceNetwork(String message, String direction) {
		TRACE_LOGGER.info("<{}> {}", direction, personalizeMessage(message));
	}

	private String personalizeMessage(String message) {
		if (name.isEmpty()) {
			return message;
		}

		return String.format("[%s] %s", name, message);
	}
}
