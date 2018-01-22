package mySystem;

public class projectMain {

    public static void main(String[] args) {
        String sampleDataFile = "sampleData.txt" ;
        String newDdataFile = "" ;

        indexShapingClass indexShape = new indexShapingClass() ;
        indexShape.indexShaping(sampleDataFile) ;
    }

}
