package mySystem;

public class myGeoTrend {

    int depth ;
    myCell firstCell ;
    int k ;
    int N ;

    myGeoTrend(int k, int N) {
        this.depth = 1 ;
        this.firstCell = new myCell(-180.0, 180.0, -90.0, 90.0, 0, k, N) ;
        this.k = k ;
        this.N = N ;
    }

    public void newKeyword (String keyword, myPoint point, long timestamp) {
        int ret ;

        ret = firstCell.addKeyword(keyword, point, timestamp) ;

        System.out.println("ret = " + ret) ;

        System.out.println("----------------------------------------- printCell - start");
        firstCell.printCell() ;
        System.out.println("----------------------------------------- printCell - end");
    }

}
