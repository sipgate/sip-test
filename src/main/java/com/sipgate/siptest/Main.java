package com.sipgate.siptest;

import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.type.extension.E;
import com.sipgate.type.extension.Extension;
import java.nio.file.Paths;
import org.slf4j.Logger;

public class Main {

	private static final Logger LOGGER = getLogger(Main.class);

	private static final E EXTENSION_0 = (E) Extension.parse("2477148e0").get();
	private static final String PASSWORD_0 = "W6oaVgRhdY4H";
	private static final String NUMBER_0 = "020387844335";

	public static void main(String[] args) {
		final SipgateUserAgent userAgent = SipgateUserAgent
				.newBuilder()
				.forExtension(EXTENSION_0)
				.withSecret(PASSWORD_0)
				.withName("Caller")
				.withAudioOutput(Paths.get("/tmp/audio.out"))
				.build();

		LOGGER.info("Arming soft phone");

		userAgent.incomingFuture().thenAccept(callId -> {
			LOGGER.info("Picking up {}", callId);
			userAgent.pickup();
		});

		userAgent.remoteHangupFuture().thenAccept(result -> {
			LOGGER.info("Callee hung up");
//			userAgent.shutdown();
			System.exit(0);
		});

		LOGGER.info("Registering");

		userAgent.register();
	}
}
