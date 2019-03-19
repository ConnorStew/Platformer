package GameUtils;

import java.io.*;
import javax.sound.sampled.*;

/**
 * This class is used to load and play sound files.
 * @author Connor Stewart
 */
public class Sound {

	/** The sound file location. */
	private String fileLocation;

	/** The loaded sound clip. */
	private Clip soundClip;

	/**
	 * Creates a new sound by loading a file from the given location.
	 * @param fileLocation the sound files location
	 */
	public Sound(String fileLocation) {
		this.fileLocation = fileLocation;
		try {
			loadSound();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			System.err.println("Failed to load sound: " + fileLocation);
			e.printStackTrace();
		}
	}

	/**
	 * Loads the sound from the given fileLocation.
	 * @throws LineUnavailableException error loading the sound file
	 * @throws IOException error loading the sound file
	 * @throws UnsupportedAudioFileException error loading the sound file
	 */
	private void loadSound() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File file = new File(fileLocation);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);

		soundClip = AudioSystem.getClip();
		soundClip.open(stream);
	}

	/**
	 * Plays the sound clip on a new thread.
	 */
	public void play() {
		new Thread(() -> {
			soundClip.setFramePosition(0);
			soundClip.setMicrosecondPosition(0);
			soundClip.start();
		});
	}
}
