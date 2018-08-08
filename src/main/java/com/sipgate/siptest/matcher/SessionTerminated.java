package com.sipgate.siptest.matcher;

import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.siptest.SipgateUserAgent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Logger;

public class SessionTerminated extends BaseMatcher<SipgateUserAgent> {

	private static final Logger LOGGER = getLogger(SessionTerminated.class);

	private final int value;
	private final TimeUnit unit;

	public SessionTerminated(int value, TimeUnit unit) {
		this.value = value;
		this.unit = unit;
	}

	@Override
	public boolean matches(Object item) {
		try {
			return ((SipgateUserAgent) item).terminateFuture().get(value, unit);
		} catch (TimeoutException e) {
			LOGGER.error("Useragent did not terminate within {} {}", value, unit);
			return false;
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Exception while waiting for termination", e);
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("Did not terminate for " )
				.appendValue(value)
				.appendText(" ")
				.appendValue(unit);
	}
}
