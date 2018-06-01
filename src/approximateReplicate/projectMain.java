package approximateReplicate;

public class projectMain {

    public static void main(String[] args) {
        //String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\data.txt" ;
        String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallData.txt" ;
        //String newDataFile = "" ;
        int k = 2 ;
        int N = 4 ; // N counters - T/N
        int T = 8 ; // T time units - number of time units that we keep data
        double e = 0.25 ;

        myGeoTrend geoTrend = new myGeoTrend(k, N, T, e) ;

        insertKeywordsClass indexShape = new insertKeywordsClass() ;
        indexShape.insertKeywords(geoTrend, sampleDataFile) ;
    }

}
