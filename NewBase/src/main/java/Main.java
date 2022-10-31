import webSite.SiteConnect;

import java.io.IOException;

public class Main {

    private static final String stringUrl = "http://www.playback.ru/";

    public static void main(String[] args) throws IOException {

        long timeStart = System.currentTimeMillis();

        SiteConnect siteConnect = new SiteConnect();
//        siteConnect.webConnect(stringUrl);
//        System.out.println(" = " + siteConnect.getStringList());

        ResultBase resultBase = new ResultBase();
        //resultBase.addResult(stringUrl);

        System.out.println("Timer - " + (System.currentTimeMillis() - timeStart) + " ms.");
    }
}