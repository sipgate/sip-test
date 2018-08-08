package com.sipgate.siptest;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.text.MessageFormat.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.sourceforge.peers.media.AbstractSoundManager;
import org.slf4j.Logger;

public class SoundManager extends AbstractSoundManager {

	private Path outfile;
	private OutputStream outputStream;

	private static final Logger LOGGER = getLogger(SoundManager.class);

	public SoundManager() {
		this(null);
	}

	public SoundManager(Path outfile) {
		this.outfile = outfile;
	}

	public void init() {
		if (outfile == null) {
			return;
		}

		try {
			outputStream = Files.newOutputStream(outfile, TRUNCATE_EXISTING, CREATE);
			LOGGER.info("Writing incoming audio into {}", outfile);
		} catch (IOException e) {
			throw new RuntimeException(format("Failed to intialize output stream into file {0}", outfile), e);
		}
	}

	public synchronized void close() {
		if (outputStream == null) {
			LOGGER.info("Closing sound manager");
			return;
		}

		try {
			LOGGER.debug("Closing stream for {}", outfile);
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error("Failed to close output stream", e);
		}
	}

	public synchronized byte[] readData() {
		return new byte[]{0};
	}

	public int writeData(byte[] buffer, int offset, int length) {
		if (outputStream == null) {
			return length;
		}

		try {
			outputStream.write(buffer, offset, length);
			return length;
		} catch (IOException e) {
			LOGGER.error("cannot write to file", e);
			return -1;
		}
	}

	public Path getOutfile() {
		return outfile;
	}
}
