package com.sipgate.siptest.matcher;

import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.siptest.SipgateUserAgent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;

public class RemotePickup extends BaseMatcher<SipgateUserAgent> {

	private static final Logger LOGGER = getLogger(RemotePickup.class);

	private final int value;
	private final TimeUnit unit;

	public RemotePickup(int value, TimeUnit unit) {
		this.value = value;
		this.unit = unit;
	}

	@Override
	public boolean matches(Object item) {
		SipgateUserAgent agent = (SipgateUserAgent) item;

		try {
			int ringingStatus = agent.remotePickupFuture().get(value, TimeUnit.SECONDS);
			if (ringingStatus == 200) {
				return true;
			}
		} catch (TimeoutException e) {
			LOGGER.error("Useragent did received no remote pickup within {} {}", value, unit);
			return false;
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Exception while waiting for remote pickup event", e);
			return false;
		}

		return true;
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("useragent should receive remote pickup within ")
				.appendValue(value)
				.appendText(" seconds.");
	}
}
