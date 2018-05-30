package fixedShapingUncertain;

public class projectMain {

    public static void main(String[] args) {
        //String sampleDataFile = "sampleData.txt" ;
        String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallDataUncertain.txt" ;
        //String newDataFile = "" ;
        int k = 2 ;
        int N = 4 ; // N counters - T/N
        int T = 8 ; // T time units - number of time units that we keep data
        //double e = 0.25 ;

        myGeoTrend geoTrend = new myGeoTrend(k, N, T) ;

        indexShapingClass indexShape = new indexShapingClass() ;
        indexShape.insertKeywords(geoTrend, sampleDataFile) ;

        System.out.println();
        System.out.println("--------------------> Finish index creation - Start real data <--------------------");
        System.out.println();

        insertKeywordsClass insertKeywords = new insertKeywordsClass() ;
        insertKeywords.insertKeywords(geoTrend, sampleDataFile);
    }

}
