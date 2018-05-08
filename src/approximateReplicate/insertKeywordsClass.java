package approximateReplicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class insertKeywordsClass {

    public void insertKeywords(myGeoTrend geoTrend, String sampleDataFile) {

        int timer = 0 ;

        try {
            File file = new File(sampleDataFile) ;
            FileReader fileReader = null ;
            fileReader = new FileReader(file) ;
            BufferedReader bufferedReader = new BufferedReader(fileReader) ;

            String line ;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lineList = Arrays.asList(line.split(",")) ;
                for (int i = 0 ; i < lineList.size() ; i++) {
                    System.out.print(lineList.get(i) + " <> ") ;
                }
                System.out.println() ;

                String keyword = (lineList.get(3)).substring(1) ;
                //double longitude = Double.parseDouble(lineList.get(1)) ;
                //double latitude = Double.parseDouble(lineList.get(2)) ;
                myPoint newPoint = new myPoint(Double.parseDouble(lineList.get(1)), Double.parseDouble(lineList.get(2))) ;
                long timestamp = Long.parseLong(lineList.get(0)) ;

                geoTrend.newKeyword(keyword, newPoint, timestamp) ;
            }
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
