package fixedShaping;

public class myGeoTrend {

    int depth ;
    myCell firstCell ;
    int k ;
    int N ; // N counters - T/N
    int T ; // T time units - number of time units that we keep data
    int timer ;
    int p ; // pointer

    myGeoTrend(int k, int N, int T) {
        this.depth = 1 ;
        this.firstCell = new myCell(-180.0, 180.0, -90.0, 90.0, 0, k, N, T) ;
        this.k = k ;
        this.N = N ;
        this.T = T ;
        this.timer = 0 ;
        this.p = 0 ;
    }

    public void newKeywordForIndexing (String keyword, myPoint point, long timestamp) {
        int ret ;

        ret = firstCell.addIndexingKeyword(keyword, point,-1) ;

        System.out.println("ret = " + ret) ;

        System.out.println("-----------------------------------------------------------");

//        System.out.println("----------------------------------------- printCell - start");
//        firstCell.printCell() ;
//        System.out.println("----------------------------------------- printCell - end");
    }

}
