package mySystem;

public class projectMain {

    public static void main(String[] args) {
        //String sampleDataFile = "sampleData.txt" ;
        String sampleDataFile = "C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallData.txt" ;
        String newDdataFile = "" ;

        indexShapingClass indexShape = new indexShapingClass() ;
        indexShape.indexShaping(sampleDataFile) ;
    }

}
