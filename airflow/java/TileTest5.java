package egovframework.innopam.tile;

import org.junit.jupiter.api.Test;

import java.io.File;

public class TileTest5 {

    private final String FILE_PATH = "/Users/ymcho/dev/PM2023_JSAT_BE/share_folder";
    @Test
    public void test() {
        String tilePath = "3/3/3.png";

        // Parse the zoom, x, y values from the path
        String[] values = tilePath.split("/");
        int zoom = Integer.parseInt(values[0]); //3
        int x = Integer.parseInt(values[1]); //3
        int y = Integer.parseInt(values[2].split("\\.")[0]); //3

        // Calculate the latitude and longitude of the center of the tile
        double[] latlon = num2deg(x + 0.5, y + 0.5, zoom);

        System.out.println("Latitude: " + latlon[0] + ", Longitude: " + latlon[1]);
    }

    public double[] num2deg(double xtile, double ytile, int zoom) {
        double n = Math.pow(2.0, zoom);
        double lon_deg = xtile / n * 360.0 - 180.0;
        double lat_rad = Math.atan(Math.sinh(Math.PI * (1 - 2 * ytile / n)));
        double lat_deg = Math.toDegrees(lat_rad);
        return new double[] { lat_deg, lon_deg };
    }

}
