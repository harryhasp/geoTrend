package accurateReplicate;

import java.util.ArrayList;
import java.util.List;

class hashValue {

    //int n = 3 ;
    //private int N ;
    double[] countersN ;
    double trend ;
    List<myPoint>[] locations ;
    // list of lists for locations. First list size of n (T/N)

    hashValue(int N) {
        //this.N = N ;
        this.countersN = new double[N] ;
        for (int i = 0 ; i < (this.countersN).length ; i++) {
            (this.countersN)[i] = 0.0 ;
        }
        this.trend = (double) (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
        this.locations = new List[N] ;
        for (int i = 0 ; i < (this.locations).length ; i++) {
            (this.locations)[i] = new ArrayList<>() ;
        }
    }

}
