package com.sipgate.siptest.matcher;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.sipgate.siptest.SipgateUserAgent;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matcher;

public final class SipMatcher {
	private SipMatcher() {
		super();
	}

	public static Matcher<SipgateUserAgent> isRegistered() {
		return new IsRegistered(10, SECONDS);
	}

	public static Matcher<SipgateUserAgent> receivesRemoteRinging() {
		return new RemoteRinging(10, SECONDS);
	}

	public static Matcher<SipgateUserAgent> receivesRemoteRingingWithin(int value, TimeUnit unit) {
		return new RemoteRinging(value, unit);
	}

	public static Matcher<SipgateUserAgent> receivesRemotePickup() {
		return new RemotePickup(10, SECONDS);
	}

	public static Matcher<SipgateUserAgent> receivesRemotePickupWithin(int value, TimeUnit unit) {
		return new RemotePickup(value, unit);
	}

	public static Matcher<SipgateUserAgent> hasCalled(String number) {
		return new HasCalled(10, SECONDS, number);
	}

	public static Matcher<SipgateUserAgent> receivesRemoteHangup(int value, TimeUnit unit) {
		return new RemoteHangup(value, unit);
	}

	public static Matcher<SipgateUserAgent> receivesRemoteHangup() {
		return new RemoteHangup(10, SECONDS);
	}

	public static Matcher<SipgateUserAgent> sessionTerminated(int value, TimeUnit unit) {
		return new SessionTerminated(value, unit);
	}

	public static Matcher<SipgateUserAgent> sessionTerminated() {
		return new SessionTerminated(10, SECONDS);
	}

	public static Matcher<SipgateUserAgent> receivesIncomingCall() {
		return new IncomingCall(10, SECONDS);
	}
}
