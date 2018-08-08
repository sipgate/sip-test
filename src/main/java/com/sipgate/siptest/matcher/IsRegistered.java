package com.sipgate.siptest.matcher;

import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.siptest.SipgateUserAgent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;

public class IsRegistered extends BaseMatcher<SipgateUserAgent> {

	private static final Logger LOGGER = getLogger(IsRegistered.class);

	private final int value;
	private final TimeUnit unit;

	public IsRegistered(int value, TimeUnit seconds) {
		this.value = value;
		this.unit = seconds;
	}

	@Override
	public boolean matches(Object item) {
		try {
			((SipgateUserAgent) item).register();
			((SipgateUserAgent) item).registerFuture().get(value, unit);
			return true;
		} catch (TimeoutException e) {
			LOGGER.error("Useragent could not isRegistered for {} {}", value, unit);
			return false;
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Exception while waiting for isRegistered success event", e);
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("useragent should isRegistered within ")
				.appendValue(value)
				.appendText(" seconds.");
	}
}
