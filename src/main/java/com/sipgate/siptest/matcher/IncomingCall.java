package com.sipgate.siptest.matcher;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ser.Serializers.Base;
import com.sipgate.siptest.SipgateUserAgent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Logger;

public class IncomingCall extends BaseMatcher<SipgateUserAgent> {

	private static final Logger LOGGER = getLogger(IncomingCall.class);

	private final int value;
	private final TimeUnit unit;

	public IncomingCall(int value, TimeUnit unit) {
		this.value = value;
		this.unit = unit;
	}

	@Override
	public boolean matches(Object item) {
		try {
			((SipgateUserAgent) item).incomingFuture().get(value, unit);

			return true;
		} catch (TimeoutException e) {
			LOGGER.error("Useragent did received an incoming call within {} {}", value, unit);
			return false;
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Exception while waiting for incoming call", e);
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("Did not receive incoming call for " )
				.appendValue(value)
				.appendText(" ")
				.appendValue(unit);
	}
}
