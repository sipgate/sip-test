package com.sipgate.siptest.sip;

import net.sourceforge.peers.sip.RFC3261;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldValue;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;

public class SipUtils {
	public static SipHeaderFieldValue headerValue(SipRequest request, String header) {
		return request.getSipHeaders().get(new SipHeaderFieldName(header));
	}

	public static SipHeaderFieldValue callId(SipRequest request) {
		return request.getSipHeaders().get(new SipHeaderFieldName(RFC3261.HDR_CALLID));
	}

	public static SipHeaderFieldValue callId(SipResponse response) {
		return response.getSipHeaders().get(new SipHeaderFieldName(RFC3261.HDR_CALLID));
	}
}
