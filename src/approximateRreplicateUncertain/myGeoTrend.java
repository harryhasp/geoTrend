package approximateRreplicateUncertain;

public class myGeoTrend {

    //private int depth ;
    private myCell firstCell ;
    //private int k ;
    private int N ; // N counters - T/N
    private int T ; // T time units - number of time units that we keep data
    //private int timer ;
    private int p ; // pointer

    myGeoTrend(int k, int N, int T) {
        //this.depth = 1 ;
        this.firstCell = new myCell(-180.0, 180.0, -90.0, 90.0, 0, k, N, T) ;
        //this.k = k ;
        this.N = N ;
        this.T = T ;
        //this.timer = 0 ;
        this.p = 0 ;
    }

    void newKeyword (String keyword, myPolygon polygon, long timestamp) {
        int ret ;

        p = (int) ( (timestamp / (T/N)) % N) ;
        p = N - 1 - p ;
        System.out.println("-------------> p = " + p);

        ret = firstCell.addKeyword(keyword, polygon, timestamp, p, -1) ;

        System.out.println("ret = " + ret) ;

        System.out.println("-----------------------------------------------------------");

//        System.out.println("----------------------------------------- printCell - start");
//        firstCell.printCell() ;
//        System.out.println("----------------------------------------- printCell - end");
    }

}
