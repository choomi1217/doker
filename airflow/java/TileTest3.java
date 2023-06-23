package egovframework.innopam.tile;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;


public class TileTest3 {

    private final String FILE_PATH = "/Users/ymcho/dev/PM2023_JSAT_BE/share_folder";
    private static final int TILE_SIZE = 256;
    private static final double INITIAL_RESOLUTION = 2 * Math.PI * 6378137 / TILE_SIZE;
    private static final double ORIGIN_SHIFT = 2 * Math.PI * 6378137 / 2.0;
    @Test
    public void Tile2LatLon() throws FactoryException, TransformException, IOException {

        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3857"); // your current CRS
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326"); // standard lat/lon

        double lon_min = 0;
        double lat_min = 0;
        double lon_max = 0;
        double lat_max = 0;

        File file = new File(FILE_PATH + "/upload/Seoul_RGB.tif");
        AbstractGridFormat format = GridFormatFinder.findFormat(file);
        GridCoverageReader reader = format.getReader(file);
        GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
        Envelope2D envelope = coverage.getEnvelope2D();
        lon_min = envelope.getMinX();
        lat_min = envelope.getMinY();
        lon_max = envelope.getMaxX();
        lat_max = envelope.getMaxY();

//        DirectPosition2D src = new DirectPosition2D(latlon[0], latlon[1]);
//        DirectPosition2D dest = new DirectPosition2D();
//        transform.transform(src, dest);

        Tile2LatLon converter = new Tile2LatLon(lon_min, lat_min, lon_max, lat_max);
        double[] latlon = converter.tile2LatLon(1, 1, 3);
        System.out.println("Latitude: " + latlon[0] + ", Longitude: " + latlon[1]);


        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);

        DirectPosition2D src = new DirectPosition2D(latlon[0], latlon[1]);
        DirectPosition2D dest = new DirectPosition2D();
        transform.transform(src, dest);

        System.out.println("Translated Latitude: " + dest.y + ", Longitude: " + dest.x);

    }
    public class  Tile2LatLon {
        private double lon_min;
        private double lat_min;
        private double lon_max;
        private double lat_max;

        public Tile2LatLon(double lon_min, double lat_min, double lon_max, double lat_max) {
            this.lon_min = lon_min;
            this.lat_min = lat_min;
            this.lon_max = lon_max;
            this.lat_max = lat_max;
        }
        public double[] tile2LatLon(int tx, int ty, int zoom) {
            double[] latlon = new double[2];

            // 전체 타일 수 계산
            int totalTiles = (int) Math.pow(2, zoom);

            // 타일의 상대적 위치 계산
            double x_rel = (double) tx / totalTiles;
            double y_rel = (double) ty / totalTiles;

            // 실제 위경도 위치 계산
            double lon = lon_min + x_rel * (lon_max - lon_min);
            double lat = lat_min + y_rel * (lat_max - lat_min);

            latlon[0] = lat;
            latlon[1] = lon;
            return latlon;
        }
    }

}
