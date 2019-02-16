package GameUtils;

import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class TiledTileMap {

    private HashMap<Pair<Integer, Integer>, Tile> tiles = new HashMap<>();
    private HashMap<Integer, Image> tileImages = new HashMap<>();
    private int height;
    private int width;

    public TiledTileMap(String jsonFileLocation) throws InvalidArgumentException {
        String jsonTextContent = null;
        try {
            InputStream is = new FileInputStream(jsonFileLocation);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while(line != null){
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            jsonTextContent = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonTextContent == null)
            throw new InvalidArgumentException(new String[]{jsonFileLocation + " does not exist."});

        JSONObject base = new JSONObject(jsonTextContent);
        JSONArray data = base.getJSONArray("layers").getJSONObject(0).getJSONArray("data");
        width = base.getJSONArray("layers").getJSONObject(0).getInt("width");
        height = base.getJSONArray("layers").getJSONObject(0).getInt("height");

        JSONArray tilesJSON = base.getJSONArray("tilesets").getJSONObject(0).getJSONArray("tiles");
        int firstGID = base.getJSONArray("tilesets").getJSONObject(0).getInt("firstgid");

        for (int i = 0; i < tilesJSON.length(); i++) {
            JSONObject entry = tilesJSON.getJSONObject(i);

            int id = entry.getInt("id");
            String location = entry.getString("image");

            tileImages.put(id, new ImageIcon(location).getImage());
            System.out.println(tilesJSON.getJSONObject(i));
        }

        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int id = data.getInt(count);
                System.out.println("x:" + x + ", y:" + y + ", id:" + id);

                if (id != 0) {
                    Tile tile = new Tile(tiles.values(), tileImages.get(id - firstGID), 16,16, x, y);
                    tiles.put(new Pair<>(x,y), tile);
                }

                count++;
            }
        }

    }

    public void draw(Graphics2D g, int xo, int yo) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = tiles.get(new Pair<>(x,y));

                if (tile != null) {
                    Image img = tile.getImage();

                    int drawX = (x * tile.getWidth()) - xo;
                    int drawY =  (y * tile.getHeight()) - yo;
                    g.drawImage(img, drawX, drawY,null);
                }
            }
        }
    }

    public Collection<Tile> getTiles() {
        return tiles.values();
    }
}
