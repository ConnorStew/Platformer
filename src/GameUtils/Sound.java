package GameUtils;

import java.io.*;
import javax.sound.sampled.*;

/**
 * This class handles loading and paying sounds.
 * @author Connor Stewart
 */
public class Sound {

	/** The sound file location. */
	private String fileLocation;

	/** The loaded sound clip. */
	private Clip soundClip;

	/**
	 * Initialises the sound from the given location.
	 * @param fileLocation the location of the sound file
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
	 * Loads the sound.
	 * @throws LineUnavailableException error loading sound
	 * @throws IOException error loading sound
	 * @throws UnsupportedAudioFileException error loading sound
	 */
	private void loadSound() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File file = new File(fileLocation);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);

		soundClip = AudioSystem.getClip();
		soundClip.open(stream);
	}

	/**
	 * Plays the sound on a new thread.
	 */
	public void play() {
		soundClip.setFramePosition(0);
		soundClip.setMicrosecondPosition(0);

		new Thread(() -> soundClip.start()).start();
	}
}