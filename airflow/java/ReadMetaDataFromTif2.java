package egovframework.innopam.tile;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.imageio.geotiff.GeoTiffIIOMetadataDecoder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.jupiter.api.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class ReadMetaDataFromTif2 {
    @Test
    public void test() throws IOException {
        String filePath = "/Users/ymcho/dev/PM2023_JSAT_BE/share_folder/upload/Seoul_RGB.tif";
        GeoTiffReader reader = new GeoTiffReader(new File(filePath));

        GridCoverage2D coverage = reader.read(null);
        GeoTiffIIOMetadataDecoder metadata = reader.getMetadata();

        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
        System.out.println("CRS: " + crs);

        AffineTransform transform = (AffineTransform) coverage.getGridGeometry().getGridToCRS();

        // Extract the GeoTransform parameters
        double[] geoTransform = new double[6];
        transform.getMatrix(geoTransform);

        System.out.println("GeoTransform: ");
        System.out.println("  elt_0_0: " + geoTransform[0]);
        System.out.println("  elt_0_1: " + geoTransform[1]);
        System.out.println("  elt_0_2: " + geoTransform[2]);
        System.out.println("  elt_1_0: " + geoTransform[3]);
        System.out.println("  elt_1_1: " + geoTransform[4]);
        System.out.println("  elt_1_2: " + geoTransform[5]);


        reader.dispose();

    }
}
