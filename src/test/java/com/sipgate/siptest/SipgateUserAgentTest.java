package com.sipgate.siptest;

import static com.sipgate.siptest.matcher.SipMatcher.hasCalled;
import static com.sipgate.siptest.matcher.SipMatcher.isRegistered;
import static com.sipgate.siptest.matcher.SipMatcher.receivesRemoteHangup;
import static com.sipgate.siptest.matcher.SipMatcher.receivesRemotePickup;
import static com.sipgate.siptest.matcher.SipMatcher.receivesRemoteRinging;
import static com.sipgate.siptest.matcher.SipMatcher.sessionTerminated;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.siptest.matcher.SipMatcher;
import com.sipgate.type.extension.E;
import com.sipgate.type.extension.Extension;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class SipgateUserAgentTest {

	private static final Logger LOGGER = getLogger(SipgateUserAgentTest.class);

	private static final Properties  ACCOUNT_PROPS = new Properties();

	static {
		try {
			ACCOUNT_PROPS.load(SipgateUserAgentTest.class.getResourceAsStream("/account.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final E EXTENSION_0 = (E) Extension.parse(ACCOUNT_PROPS.getProperty("extension.0")).get();
	private static final String PASSWORD_0 = ACCOUNT_PROPS.getProperty("password.0");
	private static final String NUMBER_0 = ACCOUNT_PROPS.getProperty("number.0");

	private static final E EXTENSION_1 = (E) Extension.parse(ACCOUNT_PROPS.getProperty("extension.1")).get();
	private static final String PASSWORD_1 = ACCOUNT_PROPS.getProperty("password.1");
	private static final String NUMBER_1 = ACCOUNT_PROPS.getProperty("number.1");

	private SipgateUserAgent userAgent0;
	private SipgateUserAgent userAgent1;

	@Before
	public void buildUserAgent() {
		userAgent0 = SipgateUserAgent
				.newBuilder()
				.forExtension(EXTENSION_0)
				.withSecret(PASSWORD_0)
				.withName("Caller")
				.build();

		userAgent1 = SipgateUserAgent
				.newBuilder()
				.forExtension(EXTENSION_1)
				.withSecret(PASSWORD_1)
				.withName("Callee")
				.build();
	}

	@After
	public void terminateUserAgent() {
		userAgent0.hangup();
		userAgent1.hangup();
	}

	@Test
	public void testRegister() {
		assertThat(userAgent0, isRegistered());
	}

	@Test
	public void testRinging() {
		assertThat(userAgent1, isRegistered());
		assertThat(userAgent0, allOf(
				hasCalled(NUMBER_1),
				receivesRemoteRinging()));
	}

	@Test
	public void testDialog() throws Exception {

		//
		// Prepare dialog - pickup after 1 second, talk 2 seconds and hangup
		//

		userAgent1.incomingFuture()
				.thenAccept(status -> {
					LOGGER.info("User Agent 1 sees inbound call.");

					wait(1000);

					LOGGER.info("User Agent 1 picks up the line.");
					userAgent1.pickup();

					wait(2000);

					LOGGER.info("User Agent 1 doesn't want to speak anymore.");
					userAgent1.hangup();
				});

		//
		// start call
		//

		assertThat(userAgent1, isRegistered());
		assertThat("userAgent starts call", userAgent0, hasCalled(NUMBER_1));

		//
		// wait for all the events
		//

		assertThat(userAgent0, allOf(
				receivesRemoteRinging(),
				receivesRemotePickup(),
				receivesRemoteHangup(),
				sessionTerminated()
		));

		assertThat(userAgent1, sessionTerminated());
	}

	@Test
	public void testCallerId() {
		assertThat(userAgent0, isRegistered());

		assertThat(userAgent1, hasCalled(NUMBER_0));
		assertThat(userAgent0, SipMatcher.receivesIncomingCall());
	}

	private void wait(int pause) {
		try {
			Thread.sleep(pause);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
