package mySystem;

public class projectMain {

    public static void main(String[] args) {
        //String sampleDataFile = "sampleData.txt" ;
        String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallData.txt" ;
        String newDdataFile = "" ;
        int k = 2 ;
        int N = 4 ;

        myGeoTrend geoTrend = new myGeoTrend(k, N) ;

        indexShapingClass indexShape = new indexShapingClass() ;
        indexShape.indexShaping(geoTrend, sampleDataFile) ;
    }

}
