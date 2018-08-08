package com.sipgate.siptest.sip;

import net.sourceforge.peers.sip.core.useragent.SipListener;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;

public class SipListenerAdapter implements SipListener {

	@Override
	public void registering(SipRequest sipRequest) {

	}

	@Override
	public void registerSuccessful(SipResponse sipResponse) {

	}

	@Override
	public void registerFailed(SipResponse sipResponse) {

	}

	@Override
	public void incomingCall(SipRequest sipRequest, SipResponse sipResponse) {

	}

	@Override
	public void remoteHangup(SipRequest sipRequest) {

	}

	@Override
	public void ringing(SipResponse sipResponse) {

	}

	@Override
	public void calleePickup(SipResponse sipResponse) {

	}

	@Override
	public void error(SipResponse sipResponse) {

	}
}
