package mySystem;

public class myGeoTrend {

    int depth ;
    myCell firstCell ;
    int k ;

    myGeoTrend(int k) {
        this.depth = 1 ;
        this.firstCell = new myCell(-180.0, 180.0, -90.0, 90.0, 0, k) ;
        this.k = k ;
    }

    public void newKeyword (String keyword, myPoint point, long timestamp) {
        int ret ;

        ret = firstCell.addKeyword(keyword, point, timestamp) ;

        System.out.println("ret = " + ret) ;
    }

}
