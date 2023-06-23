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

import java.awt.geom.Point2D;
import java.io.File;

public class TileTest4 {

    private final String FILE_PATH = "/Users/ymcho/dev/PM2023_JSAT_BE/share_folder";
    private static final int TILE_SIZE = 256;
    private static final double INITIAL_RESOLUTION = 2 * Math.PI * 6378137 / TILE_SIZE;
    private static final double ORIGIN_SHIFT = 2 * Math.PI * 6378137 / 2.0;
    @Test
    public void Tile2LatLon() throws FactoryException, TransformException {
        double lon_min = 0;
        double lat_min = 0;
        double lon_max = 0;
        double lat_max = 0;

        // 타일 인덱스
        // 126.85466182631251 , 37.2195458463125
        // 126.8552997831875 , 37.2195458463125
        // 126.85382450791407 , 37.21870852791406
        int tileX = 1;
        int tileY = 1;

        try {
            File file = new File(FILE_PATH + "/upload/out.tif");
            AbstractGridFormat format = GridFormatFinder.findFormat(file);
            GridCoverageReader reader = format.getReader(file);
            GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
            Envelope2D envelope = coverage.getEnvelope2D();
            lon_min = envelope.getMinX();
            lat_min = envelope.getMinY();
            lon_max = envelope.getMaxX();
            lat_max = envelope.getMaxY();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double tifWidth = lon_max - lon_min;
        double tifHeight = lat_max - lat_min;

        // 각 줌 레벨에서의 타일 갯수
        int numTiles = 1 << 6; // 2^zoom

        // 각 타일의 너비와 높이
        double tileWidth = tifWidth / numTiles;
        double tileHeight = tifHeight / numTiles;

        // 타일의 중심점
        double centerX = lon_min + (tileX + 0.5) * tileWidth;
        double centerY = lat_min + (tileY + 0.5) * tileHeight;

        Point2D.Double aDouble = new Point2D.Double(centerX, centerY);

        System.out.println(aDouble.x + " , " + aDouble.y);

    }
}
