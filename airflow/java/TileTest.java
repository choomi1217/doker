package egovframework.innopam.tile;

import org.junit.jupiter.api.Test;

public class TileTest {
    private final double EarthRadius = 6378137;
    private final double MinLatitude = -85.05112878;
    private final double MaxLatitude = 85.05112878;
    private final double MinLongitude = -180;
    private final double MaxLongitude = 180;

    public double Clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    public double MapSize(int levelOfDetail) {
        return 256 << levelOfDetail;
    }

    public double GroundResolution(double latitude, int levelOfDetail) {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / MapSize(levelOfDetail);
    }

    public double MapScale(double latitude, int levelOfDetail, int screenDpi) {
        return GroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }

    public void LatLongToPixelXY(double latitude, double longitude, int levelOfDetail, int pixelX, int pixelY) {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        longitude = Clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        double mapSize = MapSize(levelOfDetail);
        pixelX = (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);
        pixelY = (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
    }

    public latitudeAndLongitude PixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail) {
        double mapSize = MapSize(levelOfDetail);
        double x = (Clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (Clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double longitude = 360 * x;

        return new latitudeAndLongitude(latitude, longitude);
    }

    public class latitudeAndLongitude
    {
        public double latitude;
        public double longitude;

        public latitudeAndLongitude(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Test
    public void test(){

        int tileX = 0;
        int tileY = 0;
        int zoomLevel = 0;

        latitudeAndLongitude latitudeAndLongitude = PixelXYToLatLong(tileX, tileY, zoomLevel);
        double northLat = latitudeAndLongitude.latitude;
        double westLon = latitudeAndLongitude.longitude;

        TileTest.latitudeAndLongitude latitudeAndLongitude1 = PixelXYToLatLong(tileX + 256, tileY + 256, zoomLevel);
        double southLat = latitudeAndLongitude1.latitude;
        double eastLon = latitudeAndLongitude1.longitude;


        double centerLat = (northLat + southLat) / 2.0;
        double centerLon = (westLon + eastLon) / 2.0;

        System.out.println("northLat: " + northLat);
        System.out.println("westLon: " + westLon);
        System.out.println("southLat: " + southLat);
        System.out.println("eastLon: " + eastLon);
        System.out.println("centerLat: " + centerLat);
        System.out.println("centerLon: " + centerLon);
    }

}
