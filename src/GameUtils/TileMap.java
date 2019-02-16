package GameUtils;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

enum MapLoadState { PARSING_VARIABLES, PARSING_BASE, PARSING_INSERT }

/**
 * Tilemap built to make map-making easier.
 */
public class TileMap {

	private HashMap<Pair<Integer, Integer>, Tile> tiles = new HashMap<>();
	private HashMap<Character, Image> tileImages = new HashMap<>();
	private int mapHeight = 0;
	private int mapWidth = 0;

	/**
	 * Loads a tilemap found in the maps folder.
	 * @param name the name of the tilemap file
	 */
	public TileMap(String name) {
		Stack<String> lines = new Stack<>();

		//read lines
		try {
			BufferedReader in = new BufferedReader(new FileReader("maps/" + name));
			while (in.ready())
				lines.push(in.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}

		MapLoadState state = MapLoadState.PARSING_VARIABLES;

		int lineCount = 0; //the amount of lines in the map
		int maxLineWidth = 0;

		for (String line : lines) {

			if (line.equals("#base")) {
				state = MapLoadState.PARSING_BASE;
				continue;
			}

			if (line.equals("#insert")) {
				state = MapLoadState.PARSING_INSERT;
				continue;
			}

			if (state.equals(MapLoadState.PARSING_VARIABLES))
				parseVariable(line);

			if (state.equals(MapLoadState.PARSING_BASE)) {
				int currentLineWidth = 0;

				//go through all characters in the line
				for (int i = 0; i < line.length(); i++) {
					char character = line.charAt(i);
					StringBuilder numberString = new StringBuilder();

					if (i + 1 < line.length() && line.charAt(i+1) == '[') { //is there the beginning of an array annotation?
						int index = i;
						char currentChar;

						do {
							currentChar = line.charAt(index + 1);
							numberString.append(currentChar);
							index++;
						} while (currentChar != ']');

						int numberOfTiles = Integer.parseInt(numberString.substring(1, numberString.length() - 1).toString());

						for (int j = 0; j < numberOfTiles; j++) {
							addTile(character, currentLineWidth, lineCount);
							currentLineWidth++;
						}

						i = i + numberString.length(); //move the i pointer past the array declaration
					} else {
						addTile(character, currentLineWidth, lineCount);
						currentLineWidth++;
					}
				}

				if (currentLineWidth > maxLineWidth)
					maxLineWidth = currentLineWidth;

				lineCount++;
			}

			if (state.equals(MapLoadState.PARSING_INSERT)) {
				StringBuilder numString = new StringBuilder();
				char currentChar;
				int i = 1;

				do {
					currentChar = line.charAt(i);
					numString.append(currentChar);
					i++;
				} while (currentChar != ')');
				int xCoordinate = Integer.parseInt(numString.substring(0, numString.length() - 1));


				numString = new StringBuilder();
				do {
					currentChar = line.charAt(i);
					numString.append(currentChar);
					i++;
				} while (currentChar != ')');
				int yCoordinate = Integer.parseInt(numString.substring(1, numString.length() - 1));

				char imageChar = line.charAt(i);
				i++;

				numString = new StringBuilder();
				do {
					currentChar = line.charAt(i);
					numString.append(currentChar);
					i++;
				} while (currentChar != ']');
				int amount = Integer.parseInt(numString.substring(1, numString.length() - 1));

				for (int x = xCoordinate; x < xCoordinate + amount; x++)
					addTile(imageChar, x, yCoordinate);
			}
		}

		mapHeight = lineCount;
		mapWidth = maxLineWidth;



		//fill in lines with empty tiles if they are below max width
		for (int y = 0; y < mapHeight; y++)
			for (int x = 0; x < mapWidth; x++)
				if (tiles.get(new Pair<>(x,y)) == null)
					addTile('.', x, y);
	}

	private void addTile(char imageCharacter, int x, int y) {
		if (imageCharacter == '.')
			return;

		Image tileImage = tileImages.get(imageCharacter);
		int width = (tileImage == null) ? 16 : tileImage.getWidth(null);
		int height = (tileImage == null) ? 16 : tileImage.getHeight(null);

		tiles.put(new Pair<>(x, y), new Tile(tiles.values(),tileImage, width, height,x,y));
	}

	private void parseVariable(String line) {
		if (line.charAt(2) != '=') {
			System.err.println("Image declaration format incorrect!");
			System.exit(-1);
		}

		char imageChar = line.charAt(1);
		Image image = new ImageIcon("images/" + line.substring(3)).getImage();

		if (image.getWidth(null) == -1 && image.getHeight(null) == -1) {
			System.err.println("Image \"" + line.substring(3) + "\" not found!");
			System.exit(-1);
		}

		tileImages.put(imageChar, image);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				Tile tile = tiles.get(new Pair<>(x,y));
				if (tile != null) {
					//s//b.append(tile.imageChar);
				}
			}
			sb.append("\n");
		}


		return sb.toString();
	}

	public void draw(Graphics2D g, int xoff, int yoff) {
		//Rectangle rect = (Rectangle)g.getClip();
		//int xc,yc;

		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				Tile tile = tiles.get(new Pair<>(x,y));
				if (tile == null) continue;
				Image img = tile.getImage();
				if (img == null) continue;

				//xc = xoff + x * (int)tile.getWidth();
				//yc = yoff + y * (int)tile.getHeight();

				// Only draw the tile if it is on screen, otherwise go back round the loop
				//if (xc+tile.getWidth() < 0 || xc >= rect.x + rect.width) continue;
				//if (yc+tile.getHeight() < 0 || yc >= rect.y + rect.height) continue;
				//g.drawImage(img,xc,yc,null);


				int drawX = x * (int)tile.getWidth();
				int drawY =  y * (int)tile.getHeight();
				//System.out.println("Drawing at " + drawX + ", " + drawY);
				g.drawImage(img, drawX, drawY,null);
			}
		}

	}

	public Collection<Tile> getTiles() {
		return tiles.values();
	}
}
