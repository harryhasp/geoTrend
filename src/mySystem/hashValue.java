package mySystem;

public class hashValue {

    int n = 3 ;

    int[] countersN ;
    int tRrend ;
    myPoint location ;
    // list of lists for locations. First list size of n (T/N)

    hashValue() {
        this.countersN = new int[n] ;
        for (int i = 0 ; i < (this.countersN).length ; i++ ) {
            (this.countersN)[i] = 0 ;
        }
        this.tRrend = tRrend ;
        this.location = new myPoint(Double.MAX_VALUE, Double.MAX_VALUE) ;
    }

}
