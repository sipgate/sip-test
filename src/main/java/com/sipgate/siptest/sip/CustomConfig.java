package com.sipgate.siptest.sip;

import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.siptest.exception.UnexpectedSiptestException;
import com.sipgate.type.extension.E;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import net.sourceforge.peers.Config;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.sip.syntaxencoding.SipURI;
import net.sourceforge.peers.sip.syntaxencoding.SipUriSyntaxException;
import org.slf4j.Logger;

public class CustomConfig implements Config {

	private static final Logger LOGGER = getLogger(CustomConfig.class);

	public static Config buildConfig(E register, String password) {
		return new CustomConfig(register, password);
	}

	private InetAddress publicIpAddress;

	private final String domain = "sipgate.de";
	private final int sipPort = 0;

	private final InetAddress localIpAddress;
	private final SipURI outboundProxy;
	private final E registerExtension;
	private final String password;

	private CustomConfig(E registerExtension, String password) {
		this.registerExtension = registerExtension;
		this.password = password;

		try (DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("sipgate.de"), 5060);
			localIpAddress = socket.getLocalAddress();
			LOGGER.debug("Setting local ip address to {}", localIpAddress.getHostAddress());
		} catch (UnknownHostException | SocketException e) {
			throw new UnexpectedSiptestException("Failed to initialize local ip address", e);
		}

		try {
			outboundProxy = new SipURI("sip:sipgate.de");
		} catch (SipUriSyntaxException e) {
			throw new UnexpectedSiptestException("Failed to initialize outbound proxy", e);
		}
	}

	@Override
	public InetAddress getLocalInetAddress() {
		return localIpAddress;
	}

	@Override
	public InetAddress getPublicInetAddress() {
		return publicIpAddress;
	}

	@Override
	public String getUserPart() {
		return registerExtension.toString();
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public MediaMode getMediaMode() {
		return MediaMode.captureAndPlayback;
	}

	@Override
	public String getAuthorizationUsername() {
		return getUserPart();
	}

	@Override
	public void setPublicInetAddress(InetAddress inetAddress) {
		publicIpAddress = inetAddress;
	}

	@Override
	public SipURI getOutboundProxy() {
		return outboundProxy;
	}

	@Override
	public int getSipPort() {
		return sipPort;
	}

	@Override
	public boolean isMediaDebug() {
		return false;
	}

	@Override
	public String getMediaFile() {
		return null;
	}

	@Override
	public int getRtpPort() {
		return 0;
	}

	@Override
	public void setLocalInetAddress(InetAddress inetAddress) {
	}

	@Override
	public void setUserPart(String userPart) {
	}

	@Override
	public void setDomain(String domain) {
	}

	@Override
	public void setPassword(String password) {
	}

	@Override
	public void setOutboundProxy(SipURI outboundProxy) {
	}

	@Override
	public void setSipPort(int sipPort) {
	}

	@Override
	public void setMediaMode(MediaMode mediaMode) {
	}

	@Override
	public void setMediaDebug(boolean mediaDebug) {
	}

	@Override
	public void setMediaFile(String mediaFile) {
	}

	@Override
	public void setRtpPort(int rtpPort) {
	}

	@Override
	public void save() {
	}

	@Override
	public void setAuthorizationUsername(String authorizationUsername) {
	}

}
