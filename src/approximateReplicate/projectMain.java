package approximateReplicate;

public class projectMain {

    public static void main(String[] args) {
        //String sampleDataFile = "sampleData.txt" ;
        String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallData.txt" ;
        //String newDataFile = "" ;
        int k = 2 ;
        int N = 4 ; // N counters - T/N
        int T = 8 ; // T time units - number of time units that we keep data

        myGeoTrend geoTrend = new myGeoTrend(k, N, T) ;

        insertKeywordsClass indexShape = new insertKeywordsClass() ;
        indexShape.insertKeywords(geoTrend, sampleDataFile) ;
    }

}
