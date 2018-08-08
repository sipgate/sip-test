package com.sipgate.siptest;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.sipgate.siptest.exception.UnexpectedSiptestException;
import com.sipgate.siptest.sip.CustomConfig;
import com.sipgate.siptest.sip.SipListenerAdapter;
import com.sipgate.siptest.sip.SipUtils;
import com.sipgate.siptest.sip.SipgateUserAgentBuilder;
import com.sipgate.type.extension.E;
import io.vavr.control.Try;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.sourceforge.peers.Config;
import net.sourceforge.peers.FileLogger;
import net.sourceforge.peers.media.AbstractSoundManager;
import net.sourceforge.peers.sip.core.useragent.SipListener;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SipgateUserAgent {

	private static final Logger LOGGER = LoggerFactory.getLogger(SipgateUserAgent.class);

	private static FileLogger buildLogger(String basedir) {
		return Try.
				of(() -> {
					Files.createDirectories(Paths.get(basedir, "logs"));
					return new FileLogger(basedir);
				}).
				onFailure(e -> new UnexpectedSiptestException(format("Cannot create log base dir {0}", basedir), e))
				.get();
	}

	private final UserAgent userAgent;

	private CompletableFuture<Boolean> registerFuture = new CompletableFuture<>();
	private CompletableFuture<Integer> remoteRingingFuture = new CompletableFuture<>();
	private CompletableFuture<Integer> remotePickupFuture = new CompletableFuture<>();
	private CompletableFuture<Void> remoteHangupFuture = new CompletableFuture<>();
	private CompletableFuture<Boolean> terminateFuture = new CompletableFuture<>();
	private CompletableFuture<String> incomingFuture = new CompletableFuture<>();

	private SipResponse lastResponse;
	private SipRequest lastRequest;
	private SipLogger logger;
	private String name;

	public SipgateUserAgent(E register, String secret, SipListener listener) {
		this(register, secret, listener, "");
	}

	public SipgateUserAgent(E register, String secret, SipListener listener, String name) {
		this.userAgent = initUserAgent(register, secret, listener, name, null).get();
		this.name = name;
	}

	public SipgateUserAgent(E register, String secret, SipListener listener, Path audioOutFile) {
		this.userAgent = initUserAgent(register, secret, listener, "", audioOutFile).get();
		this.name = name;
	}

	public SipgateUserAgent(E register, String secret, SipListener listener, String name, Path audioOutFile) {
		this.userAgent = initUserAgent(register, secret, listener, name, audioOutFile).get();
		this.name = name;
	}

	private Optional<UserAgent> initUserAgent(E register, String secret, SipListener listener, String name, Path outfile) {
		final UserAgent userAgent = Try
				.of(() -> {
					this.logger = new SipLogger(name);
					final Config config = CustomConfig.buildConfig(register, secret);

					final AbstractSoundManager soundManager = new SoundManager(outfile);

					return (new UserAgent(new SipTestListener(listener), config, logger, soundManager));
				})
				.onFailure(e -> LOGGER.error("Failed to build useragent", e))
				.getOrElse((UserAgent) null);

		return Optional.ofNullable(userAgent);
	}

	public CompletableFuture<Boolean> register() {
		new Thread(() -> Try
				.of(() -> {
					LOGGER.debug("Registering user agent");
					return userAgent.register();
				})
				.onFailure(e -> fireRegisterFuture(false))
		).start();
		return registerFuture();
	}

	public CompletableFuture<Boolean> call(String callee) {
		final CompletableFuture<Boolean> result = new CompletableFuture<>();
		Try.of(() -> userAgent.invite(format("sip:{0}@sipgate.de", callee), null))
				.onSuccess(request -> {
					lastRequest = request;
					result.complete(true);
				})
				.onFailure(e -> {
					lastRequest = null;
					LOGGER.error("Failed to call {}", callee);
					result.completeExceptionally(e);
				});
		return result;
	}


	public void hangup() {
		if (lastRequest != null) {
			userAgent.terminate(lastRequest);
			fireTerminateFuture();
		}
		lastRequest = null;
	}

	public void pickup() {
		userAgent.acceptCall(lastRequest, userAgent.getDialogManager().getDialog(lastResponse));
	}

	public static SipgateUserAgentBuilder newBuilder() {
		return new SipgateUserAgentBuilder();
	}

	public CompletableFuture<Integer> remoteRingingFuture() {
		return remoteRingingFuture;
	}

	public CompletableFuture<Integer> remotePickupFuture() {
		return remotePickupFuture;
	}

	public CompletableFuture<Void> remoteHangupFuture() {
		return remoteHangupFuture;
	}

	public CompletableFuture<String> incomingFuture() {
		return incomingFuture;
	}

	public CompletableFuture<Boolean> terminateFuture() {
		return terminateFuture;
	}


	public CompletableFuture<Boolean> registerFuture() {
		return this.registerFuture;
	}

	// TODO: Race conditions in fireXXX methods


	private void fireRegisterFuture(boolean success) {
		registerFuture.complete(success);
	}

	private void fireRemoteRingingFuture(SipResponse response) {
		remoteRingingFuture.complete(response.getStatusCode());
	}

	private void fireRemotePickupFuture(SipResponse response) {
		remotePickupFuture.complete(response.getStatusCode());
	}

	private void fireRemoteHangupFuture() {
		remoteHangupFuture.complete(null);
	}

	private void fireTerminateFuture() {
		terminateFuture.complete(true);
	}

	private void fireIncomingFuture(SipRequest sipRequest, SipResponse sipResponse) {
		incomingFuture.complete(SipUtils.callId(sipRequest).toString());
	}

	private class SipTestListener implements SipListener {

		private SipListener listener;

		public SipTestListener(SipListener listener) {
			this.listener = defaultIfNull(listener, new SipListenerAdapter());
		}

		private String personalizeMessage(String message) {
			if (name.isEmpty()) {
				return message;
			}

			return String.format("[%s] %s", name, message);
		}

		public void registering(SipRequest sipRequest) {
			LOGGER.debug(personalizeMessage("Event: isRegistered - {}"), sipRequest.getMethod());
			listener.registering(sipRequest);
		}

		public void registerSuccessful(SipResponse sipResponse) {
			LOGGER.debug(personalizeMessage("Event: isRegistered success - {} {}"), sipResponse.getStatusCode(), sipResponse.getReasonPhrase());
			fireRegisterFuture(true);
			listener.registerSuccessful(sipResponse);
		}

		public void registerFailed(SipResponse sipResponse) {
			LOGGER.debug(personalizeMessage("Event: isRegistered failed - {} {}"), sipResponse.getStatusCode(), sipResponse.getReasonPhrase());
			fireRegisterFuture(false);
			listener.registerFailed(sipResponse);

		}

		public void incomingCall(SipRequest sipRequest, SipResponse sipResponse) {
			LOGGER.debug(personalizeMessage("Event: incoming call - {} {}"), sipResponse.getStatusCode(), sipResponse.getReasonPhrase());
			lastRequest = sipRequest;
			lastResponse = sipResponse;
			fireIncomingFuture(sipRequest, sipResponse);
			listener.incomingCall(sipRequest, sipResponse);
		}

		public void remoteHangup(SipRequest sipRequest) {
			LOGGER.debug(personalizeMessage("Event: remote hangup - {}"), sipRequest.getMethod());
			lastRequest = sipRequest;
			fireTerminateFuture();
			fireRemoteHangupFuture();
			listener.remoteHangup(sipRequest);
		}

		public void ringing(SipResponse sipResponse) {
			LOGGER.debug(personalizeMessage("Event: ringing - {} {}"), sipResponse.getStatusCode(), sipResponse.getReasonPhrase());
			lastResponse = sipResponse;
			fireRemoteRingingFuture(sipResponse);
			listener.ringing(sipResponse);
		}

		public void calleePickup(SipResponse sipResponse) {
			LOGGER.debug(personalizeMessage("Event: pickup - {} {}"), sipResponse.getStatusCode(), sipResponse.getReasonPhrase());
			lastResponse = sipResponse;
			fireRemotePickupFuture(sipResponse);
			listener.calleePickup(sipResponse);
		}

		public void error(SipResponse sipResponse) {
			LOGGER.debug(personalizeMessage("Event: error - {} {}"), sipResponse.getStatusCode(), sipResponse.getReasonPhrase());
			lastResponse = sipResponse;
			listener.error(sipResponse);
		}
	}


}
