package egovframework.innopam.tile;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class ReadMetaDataFromTif3 {

    @DisplayName("GeoTiff 파일에서 GeoTransform 파라미터 추출")
    @Test
    public void test() throws IOException {
        File geoTiffFile = new File("/Users/ymcho/dev/PM2023_JSAT_BE/share_folder/upload/Seoul_RGB.tif");
        AbstractGridFormat format = GridFormatFinder.findFormat(geoTiffFile);
        GridCoverage2DReader reader = format.getReader(geoTiffFile);

        if (reader == null) {
            System.out.println("Failed to read the GeoTIFF file.");
            return;
        }

        GridCoverage2D coverage = reader.read(null);
        AffineTransform geoTransform = (AffineTransform) coverage.getGridGeometry().getGridToCRS();
        System.out.println("GeoTransform: " + geoTransform);

        /*
        * "elt_0_0", "elt_0_2", "elt_1_1", "elt_1_2"는 각각 GDAL의
        * w-e pixel resolution, top left x, n-s pixel resolution, top left y와 일치합니다
        * PARAMETER["elt_0_0", 0.5], PARAMETER["elt_0_2", 952787.25], PARAMETER["elt_1_1", -0.5], PARAMETER["elt_1_2", 1954801.75]
        * */

        System.out.println(geoTransform.getScaleX()); //0.5, elt_0_0
        System.out.println(geoTransform.getScaleY()); //-0.5, elt_1_1
        System.out.println(geoTransform.getTranslateX()); //952787.25, elt_0_2
        System.out.println(geoTransform.getTranslateY()); //1954801.75, elt_1_2


    }

}
