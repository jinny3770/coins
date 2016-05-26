package coins.hansung.way.etc;

/**
 * Created by sora on 2016-05-19.
 */
public class Links {

    public final static String serverURL = "http://52.79.124.54";

    public final static String loginURL = serverURL + "/login.php";
    public final static String deleteURL = serverURL + "/deleteDestinations.php";
    public final static String loadURL = serverURL + "/loadDestinationsList.php";
    public static final String walkURL = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1";
    public static final String bicycleURL = "https://apis.skplanetx.com/tmap/routes/bicycle?version=1";
    public static final String carURL = "https://apis.skplanetx.com/tmap/routes?version=1";
    public static final String resistURL = serverURL + "/destinationsResist.php";

    public static final String myPointResistURL = serverURL + "/myPointResist.php";
    public static final String loadFamilyURL = serverURL + "/loadFamilyList.php";
    public static final String updateLocationURL = serverURL + "/updateLocation.php";

    public static final String updateGpsSigURL = serverURL + "/updateGpsSignal.php";
}
