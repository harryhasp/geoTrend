package mySystem;

import java.util.ArrayList;
import java.util.List;

public class hashValue {

    //int n = 3 ;
    int N ;
    int[] countersN ;
    int tRrend ;
    List<myPoint>[] locations ;
    // list of lists for locations. First list size of n (T/N)

    hashValue(int N) {
        this.N = N ;
        this.countersN = new int[N] ;
        for (int i = 0 ; i < (this.countersN).length ; i++) {
            (this.countersN)[i] = 0 ;
        }
        this.tRrend = tRrend ;
        this.locations = new List[N] ;
        for (int i = 0 ; i < (this.locations).length ; i++) {
            (this.locations)[i] = new ArrayList<>() ;
        }

        //this.location = new ArrayList<>() ;
        //this.location = new myPoint(Double.MAX_VALUE, Double.MAX_VALUE) ;
    }

}
