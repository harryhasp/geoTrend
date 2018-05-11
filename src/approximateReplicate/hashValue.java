package approximateReplicate;

class hashValue {

    //int n = 3 ;
    //private int N ;
    double[] countersN ;
    double trend ;

    hashValue(int N) {
        //this.N = N ;
        this.countersN = new double[N] ;
        for (int i = 0 ; i < (this.countersN).length ; i++) {
            (this.countersN)[i] = 0.0 ;
        }
        this.trend = (double) (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
    }

}
