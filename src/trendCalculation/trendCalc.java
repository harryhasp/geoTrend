package trendCalculation;

public class trendCalc {

    public static double trendCalculation(int N, int p, int[] countersN) {
        double retTrend ;
        int down = N*(N+1)*(2*N+1) ;
        //System.out.println("--> down : " + down);
        int up = 0 ;
        int cO = (p+N-1)%N ;
        //System.out.println("--> cO : " + cO);
        int multiplier = N-1 ;
        for (int i = 0 ; i < N - 1 ; i++) {
            int tempP = (i+p) % N ;
            up = up + (multiplier * (countersN[tempP] - countersN[cO])) ;
            System.out.println("--> up : " + up);
            multiplier-- ;
        }
        //System.out.println("--> up : " + up);
        retTrend = (double) (6*up) / down ;
        return (double) (6*up) / down ;
    }


    public static void main(String[] args) {
        int N = 4 ;
        int p = 0 ;
//        int[]countersN = {42, 92, 81, 65} ;
//        int[]countersN = {12, 72, 43, 38} ;
        int[]countersN = {1, 0, 0, 1} ;
//        int[]countersN = {22, 70, 55, 63} ;

        double ret = trendCalculation(N, p, countersN) ;

        System.out.println("--> " + ret);
    }
}
