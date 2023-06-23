package egovframework.innopam.tile;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.Hints;
import org.junit.jupiter.api.Test;
import org.opengis.referencing.crs.ProjectedCRS;
import org.osgeo.proj4j.*;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;


public class TileTest7 {
        private static final double TILE_SIZE = 256.0;

        public double[] getTileCenterLatLon(double[] tifTransform,String pro4j, int zoom, int x, int y) {

            double resolution = tifTransform[1] / Math.pow(2, zoom);
            double tileWidth = TILE_SIZE * resolution;
            double tileHeight = TILE_SIZE * resolution;

            double tileMinX = tifTransform[0] + x * tileWidth;
            double tileMaxY = tifTransform[3] - y * tileHeight;
            double tileMaxX = tileMinX + tileWidth;
            double tileMinY = tileMaxY - tileHeight;

            double tileCenterX = (tileMinX + tileMaxX) / 2;
            double tileCenterY = (tileMinY + tileMaxY) / 2;

            String targetCRS = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";

            CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
            CRSFactory crsFactory = new CRSFactory();

            CoordinateReferenceSystem crsFrom = crsFactory.createFromParameters("originalCRS", pro4j);
            CoordinateReferenceSystem crsTo = crsFactory.createFromParameters("EPSG:4326", targetCRS);

            CoordinateTransform transform = ctFactory.createTransform(crsFrom, crsTo);

            ProjCoordinate from = new ProjCoordinate(tileCenterX, tileCenterY);
            ProjCoordinate to = new ProjCoordinate();

            transform.transform(from, to);

            return new double[] { to.y, to.x };

        }

        @Test
        public void test() throws IOException {

            String filePath = "/Users/ymcho/dev/PM2023_JSAT_BE/share_folder/upload/Seoul_RGB.tif";
            GeoTiffReader reader = new GeoTiffReader(new File(filePath));

            double[] tifTransform = getBound(reader);
            String pro4j = getPro4j(reader);
            System.out.println("pro4j: " + pro4j);

            int zoom = 15;
            int x = 3;
            int y = 3;

            double[] latlon = getTileCenterLatLon(tifTransform, pro4j ,zoom, x, y);
            System.out.println("Latitude: " + latlon[0]);
            System.out.println("Longitude: " + latlon[1]);

            reader.dispose();
        }

    private String getPro4j(GeoTiffReader reader) throws IOException {
        // Read the coverage and metadata from the GeoTIFF
        GridCoverage2D coverage = reader.read(null);

        org.opengis.referencing.crs.CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();

        if (crs instanceof ProjectedCRS) {
            ProjectedCRS projectedCRS = (ProjectedCRS) crs;
            return projectedCRS.toWKT();
        } else {
            throw new IOException("Coordinate reference system is not projected.");
        }

    }

    private double[] getBound(GeoTiffReader reader) throws IOException {
        GridCoverage2D coverage = reader.read(null);
        AffineTransform transform = (AffineTransform) coverage.getGridGeometry().getGridToCRS();

        double[] geoTransform = new double[6];
        transform.getMatrix(geoTransform);

        return new double[] { geoTransform[5], geoTransform[0], geoTransform[1], geoTransform[4], geoTransform[2], geoTransform[3] };
    }
}