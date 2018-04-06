package mySystem;

public class projectMain {

    public static void main(String[] args) {
        //String sampleDataFile = "sampleData.txt" ;
        String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallData.txt" ;
        String newDdataFile = "" ;
        int k = 2 ;
        int N = 2 ; // N counters - T/N
        int T = 4 ; // T time units - number of time units that we keep data

        myGeoTrend geoTrend = new myGeoTrend(k, N, T) ;

        indexShapingClass indexShape = new indexShapingClass() ;
        indexShape.indexShaping(geoTrend, sampleDataFile) ;
    }

}
