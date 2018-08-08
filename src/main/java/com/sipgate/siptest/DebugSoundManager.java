package com.sipgate.siptest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.media.AbstractSoundManager;
import net.sourceforge.peers.sip.Utils;

public class DebugSoundManager  extends AbstractSoundManager {

	private AudioFormat audioFormat;
	private TargetDataLine targetDataLine;
	private SourceDataLine sourceDataLine;
	private Object sourceDataLineMutex;
	private DataLine.Info targetInfo;
	private DataLine.Info sourceInfo;
	private FileOutputStream microphoneOutput;
	private FileOutputStream speakerInput;
	private boolean mediaDebug;
	private Logger logger;
	private String peersHome;

	public DebugSoundManager(boolean mediaDebug, Logger logger, String peersHome)
	{
		this.mediaDebug = mediaDebug;
		this.logger = logger;
		this.peersHome = peersHome;
		if (peersHome == null) {
			this.peersHome = Utils.DEFAULT_PEERS_HOME;
		}
		// linear PCM 8kHz, 16 bits signed, mono-channel, little endian
		audioFormat = new AudioFormat(8000, 16, 1, true, false);
		targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		sourceDataLineMutex = new Object();
	}

	public void init() {
		logger.debug("openAndStartLines");
		if (mediaDebug) {
			SimpleDateFormat simpleDateFormat =
					new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String date = simpleDateFormat.format(new Date());
			StringBuffer buf = new StringBuffer();
			buf.append(peersHome).append(File.separator);
			buf.append("media").append(File.separator);
			buf.append(date).append("_");
			buf.append(audioFormat.getEncoding()).append("_");
			buf.append(audioFormat.getSampleRate()).append("_");
			buf.append(audioFormat.getSampleSizeInBits()).append("_");
			buf.append(audioFormat.getChannels()).append("_");
			buf.append(audioFormat.isBigEndian() ? "be" : "le");
			try {
				microphoneOutput = new FileOutputStream(buf.toString()
						+ "_microphone.output");
				speakerInput = new FileOutputStream(buf.toString()
						+ "_speaker.input");
			} catch (FileNotFoundException e) {
				logger.error("cannot create file", e);
				return;
			}
		}
		// AccessController.doPrivileged added for plugin compatibility
		AccessController.doPrivileged(
				new PrivilegedAction<Void>() {

					@Override
					public Void run() {
						try {
							targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
							targetDataLine.open(audioFormat);
						} catch (LineUnavailableException e) {
							logger.error("target line unavailable", e);
							return null;
						} catch (SecurityException e) {
							logger.error("security exception", e);
							return null;
						} catch (Throwable t) {
							logger.error("throwable " + t.getMessage());
							return null;
						}
						targetDataLine.start();
						synchronized (sourceDataLineMutex) {
							try {
								sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
								sourceDataLine.open(audioFormat);
							} catch (LineUnavailableException e) {
								logger.error("source line unavailable", e);
								return null;
							}
							sourceDataLine.start();
						}
						return null;
					}
				});

	}

	public synchronized void close() {
		logger.debug("closeLines");
		if (microphoneOutput != null) {
			try {
				microphoneOutput.close();
			} catch (IOException e) {
				logger.error("cannot close file", e);
			}
			microphoneOutput = null;
		}
		if (speakerInput != null) {
			try {
				speakerInput.close();
			} catch (IOException e) {
				logger.error("cannot close file", e);
			}
			speakerInput = null;
		}
		// AccessController.doPrivileged added for plugin compatibility
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				if (targetDataLine != null) {
					targetDataLine.close();
					targetDataLine = null;
				}
				synchronized (sourceDataLineMutex) {
					if (sourceDataLine != null) {
						sourceDataLine.drain();
						sourceDataLine.stop();
						sourceDataLine.close();
						sourceDataLine = null;
					}
				}
				return null;
			}
		});
	}

	public synchronized byte[] readData() {
			return new byte[]{10, 100, 20};
	}

	public int writeData(byte[] buffer, int offset, int length) {
		return length;
	}

}

