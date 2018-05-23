import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    Position raster;

    public Rasterer() {
    }

    /*public static void main(String[] args) {
        Position pos;
        Rasterer ras = new Rasterer();
        Map<String, Double> params = new HashMap<>();
        Map<String, Object> results;
        params.put("lrlon", -122.27478813486404);
        params.put("ullon", -122.27946649033841);
        params.put("w", 733.0709723022676);
        params.put("h", 516.7673962636325);
        params.put("ullat", 37.889902690269366);
        params.put("lrlat", 37.8889181713446);
        results = ras.getMapRaster(params);
        System.out.println(results);
        //lrlon=, ullon=, w=, h=, ullat=, lrlat=

    }*/

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (lonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        String[][] grid;
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        //return results;
        //calculate the depth of the query frame, and the determine the depth of pictures fetched
        double queryLonDPP = lonDPP(params.get("lrlon"), params.get("ullon"), params.get("w"));
        int depth = getDepth(queryLonDPP);
        //use a formula to calculate all pictures (in the same depth) lon and la,
        //store those within the range of the query box
        grid = positionToFilename(depth, params.get("lrlon"),
                params.get("ullon"), params.get("lrlat"), params.get("ullat"));
        results.put("render_grid", grid);
        results.put("raster_ul_lon", raster.ullon);
        results.put("raster_ul_lat", raster.ullat);
        results.put("raster_lr_lon", raster.lrlon);
        results.put("raster_lr_lat", raster.lrlat);
        results.put("depth", depth);
        results.put("query_success", true);
        return results;
    }

    private double lonDPP(double lrlon, double ullon, double pixel) {
        return (lrlon - ullon) / pixel;
    }

    public Position filenameToPosition(int depth, int x, int y) {
        double ullat, ullon, lrlat, lrlon;
        int num = (int) Math.pow(2, depth);
        double avgLon = Math.abs((MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / num);
        double avgLat = Math.abs((MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT) / num);
        ullon = MapServer.ROOT_ULLON + x * avgLon; //upper left lon
        ullat = MapServer.ROOT_ULLAT - y * avgLat; //upper left lat
        lrlon = MapServer.ROOT_ULLON + (x + 1) * avgLon; //lower right lon
        lrlat = MapServer.ROOT_ULLAT - (y + 1) * avgLat; //lower right lat
        return new Position(ullat, ullon, lrlat, lrlon);
    }

    private String[][] positionToFilename(int depth, double lrlon,
                                          double ullon, double lrlat, double ullat) {
        int ulX, ulY, lrX, lrY;
        int num = (int) Math.pow(2, depth);
        double avgLon = Math.abs((MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / num);
        double avgLat = Math.abs((MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT) / num);
        ulX = (int) Math.floor(Math.abs((ullon - MapServer.ROOT_ULLON)) / avgLon);
        ulY = (int) Math.floor(Math.abs((ullat - MapServer.ROOT_ULLAT)) / avgLat);
        lrX = (int) Math.ceil(Math.abs((lrlon - MapServer.ROOT_ULLON)) / avgLon);
        lrY = (int) Math.ceil(Math.abs((lrlat - MapServer.ROOT_ULLAT)) / avgLat);
        return indexToFilename(depth, ulX, ulY, lrX, lrY);
    }

    private String[][] indexToFilename(int depth, int ulX, int ulY, int lrX, int lrY) {
        int num = (int) Math.pow(2, depth);
        ulX = Math.max(0, ulX);
        ulX = Math.min(num, ulX);
        ulY = Math.max(0, ulY);
        ulY = Math.min(num, ulY);
        lrX = Math.max(0, lrX);
        lrX = Math.min(num, lrX);
        lrY = Math.max(0, lrY);
        lrY = Math.min(num, lrY);
        int width = lrX - ulX;
        int height = lrY - ulY;
        String[][] grid = new String[height][width];
        Position upper = filenameToPosition(depth, ulX, ulY);
        Position lower = filenameToPosition(depth, lrX - 1, lrY - 1);
        raster = new Position(upper.ullat, upper.ullon, lower.lrlat, lower.lrlon);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                String X = Integer.toString(ulX + i);
                String Y = Integer.toString(ulY + j);
                String d = Integer.toString(depth);
                String filename = "d" + d + "_x" + X + "_y" + Y + ".png";
                grid[j][i] = filename;
            }
        }
        return grid;
    }

    private int getDepth(double queryLonDPP) {
        //find the depth of target picture files
        double currentLonDPP;
        Position position;
        int depth = 0;
        for (depth = 0; depth < 8; depth++) {
            position = filenameToPosition(depth, 0, 0);
            currentLonDPP = lonDPP(position.lrlon, position.ullon, 256);
            if (currentLonDPP <= queryLonDPP) {
                return depth;
            }
        }
        return depth - 1;
    }

    private class Position {
        private double ullat;
        private double ullon;
        private double lrlat;
        private double lrlon;

        Position(double ullat, double ullon, double lrlat, double lrlon) {
            this.ullat = ullat;
            this.ullon = ullon;
            this.lrlat = lrlat;
            this.lrlon = lrlon;
        }
    }
}
