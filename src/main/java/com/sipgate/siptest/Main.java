package com.sipgate.siptest;

import static org.slf4j.LoggerFactory.getLogger;

import com.moandjiezana.toml.Toml;
import com.sipgate.type.extension.E;
import com.sipgate.type.extension.Extension;

import java.io.File;
import java.nio.file.Paths;
import org.slf4j.Logger;

public class Main {

	private static final Logger LOGGER = getLogger(Main.class);

	private static final E EXTENSION_0 = (E) Extension.parse("2477148e0").get();
	private static final String NUMBER_0 = "020387844335";

	public static void main(String[] args) {
		Config config = getConfig("config.toml");

		final SipgateUserAgent userAgent = SipgateUserAgent
				.newBuilder()
				.forExtension(EXTENSION_0)
				.withSecret(config.password)
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

	/**
	 * Load config.toml configuration file and create new Config instance
	 *
	 * @return Config instance initialized with contents from config.toml
	 */
	public static Config getConfig(String fileName) {
		String filePath = "";

		try {
			filePath = Main.class.getClassLoader().
					getResource(fileName).
					getFile();
		} catch (NullPointerException e) {
			LOGGER.error("Could not find config.toml", e);
			System.exit(-1);
		}

		File configFile = new File(filePath);
		return new Toml().read(configFile).to(Config.class);
	}
}
