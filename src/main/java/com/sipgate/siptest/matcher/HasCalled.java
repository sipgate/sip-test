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

public class HasCalled extends BaseMatcher<SipgateUserAgent> {

	private final int value;
	private final TimeUnit unit;
	private String number;

	private static final Logger LOGGER = getLogger(HasCalled.class);

	public HasCalled(int value, TimeUnit unit, String number) {
		this.value = value;
		this.unit = unit;
		this.number = number;
	}

	@Override
	public boolean matches(Object item) {
		try {
			return ((SipgateUserAgent) item).call(number).get(value, unit);
		} catch (TimeoutException e) {
			LOGGER.error("Useragent did received no remote pickup within {} {}", value, unit);
			return false;
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Exception while waiting for remote pickup event", e);
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("Could not send call event for " )
				.appendValue(value)
				.appendText(" ")
				.appendValue(unit);
	}
}
