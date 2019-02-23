import GameUtils.Camera;
import GameUtils.Player;
import GameUtils.Tile;
import GameUtils.TiledTileMap;
import com.sun.javaws.exceptions.InvalidArgumentException;
import game2D.GameCore;

import javax.swing.*;
import java.awt.*;

public class Platformer extends GameCore {

	private TiledTileMap tileMap;
	private Image background;
	private Player player;
	private Camera cam;
	private boolean isRunning;
	private int fps;

	public static void main(String[] args) {
		Platformer platformer = new Platformer();
		platformer.init();
		//platformer.run(false, 1200, 600);
	}

	private void init() {
		//System.out.println(tileMap);
		background = new ImageIcon("images/background0.png").getImage();
		cam = new Camera(this,600,338);
		try {
			tileMap = new TiledTileMap("D:\\Year3\\Game Dev\\Assignment\\maps\\map2.json");
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		player = new Player(tileMap.getTiles());

		setSize(1000, 500);
		setVisible(true);
        addKeyListener(this);
        addMouseListener(this);
        setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));


//		this.addComponentListener(new ComponentAdapter()  {
//			public void componentResized(ComponentEvent evt) {
//				//Component c = (Component)evt.getSource();
//
//				buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//				bg = (Graphics2D)buffer.createGraphics();
//				bg.setClip(0, 0, getWidth(), getHeight());
//			}
//		});

		cam.follow(player);

		myGameLoop();
	}

	@Override
	public void update(long elapsedTime) {
		//super.update(elapsedTime);
		Point mousePos = getContentPane().getMousePosition();
		if (mousePos == null)
			mousePos = new Point(0,0);

		int speed = 10;
		if (keysDown.contains("Right")) {
			cam.translateX(speed);
		}

		if (keysDown.contains("Left")) {
			cam.translateX(-speed);
		}

		if (keysDown.contains("Up")) {
			cam.translateY(-speed);
		}

		if (keysDown.contains("Down")) {
			cam.translateY(speed);
		}

		player.update(elapsedTime, keysDown, buttonsDown, mousePos.x, mousePos.y);
	}

	private void draw(float alpha) {
		cam.getGraphics().drawImage(background, 0, 0, background.getWidth(null), background.getHeight(null), null);
		//cam.getGraphics().drawImage(background, (int) -player.getX() / 4, (int) -player.getY() / 4, background.getWidth(null), background.getHeight(null), null);

		for (Tile tile : tileMap.getTiles())
			cam.draw(tile, alpha);

		cam.draw(player, alpha);


		cam.getGraphics().drawString("FPS: " + fps, 10, 20);

		cam.drawToScreen();
	}

	public void myGameLoop() {
		//time stuff
		final float FRAMETIME = 1000f / 60f; //16.66ms or 1000/60
		final int MAX_SKIP = 5;

		int lastSecond = 0;
		int frameCount = 0;

		long gameTimeStart = System.currentTimeMillis();
		long frameTimeStart = System.currentTimeMillis();
		float accumulator = 0; //time passed since the last update in millis
		int loopCount = 0;

		while (this.isVisible()) {
			long deltaTime = 0;
			if (System.currentTimeMillis() - frameTimeStart > 1) {
				deltaTime = System.currentTimeMillis() - frameTimeStart; //time passed for last loop
				frameTimeStart = System.currentTimeMillis();
				accumulator += deltaTime; //add time passed last loop
			}

			while (accumulator >= FRAMETIME) { //update if enough time has passed
				loopCount++;
				accumulator -= FRAMETIME;
				this.update(deltaTime);

				if (loopCount >= MAX_SKIP) {
					loopCount = 0;
					accumulator = 0;
					break;
				}
			}

			float alpha = accumulator / FRAMETIME; //the alpha is the time between the last frame and this one
			draw(alpha);

			frameCount++;

			long timePassed = System.currentTimeMillis() - gameTimeStart;
			int timePassesSeconds = (int) (timePassed / 1000);

			if (timePassesSeconds > lastSecond) {
				fps = frameCount; //Math.round((frameCount / timePassed) * 100); average fps
				lastSecond = timePassesSeconds;
				frameCount = 0;
			}
		}

		System.exit(0);
	}

}
