package egovframework.innopam.tile;

import org.junit.jupiter.api.Test;

public class TileTest6 {
        private static final double TILE_SIZE = 256.0;
        private static final double INITIAL_RESOLUTION = 2 * Math.PI * 6378137 / TILE_SIZE;
        private static final double ORIGIN_SHIFT = 2 * Math.PI * 6378137 / 2.0;

        @Test
        public void tile2LatLon() {
            int zoom = 3;
            int x = 3;
            int y = 3;
            double[] latlon = new double[2];
            double resolution = INITIAL_RESOLUTION / (Math.pow(2, zoom));

            latlon[0] = (y * TILE_SIZE * resolution - ORIGIN_SHIFT) / -1.0;
            latlon[1] = (x * TILE_SIZE * resolution - ORIGIN_SHIFT);

            latlon[0] = 180 / Math.PI * (2 * Math.atan(Math.exp(latlon[0] / 6378137)) - Math.PI / 2);
            latlon[1] = latlon[1] / 6378137 * 180.0 / Math.PI;

            System.out.println(latlon[0]);
            System.out.println(latlon[1]);
        }

}
