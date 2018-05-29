package approximateRreplicateUncertain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class insertKeywordsClass {

    void insertKeywords(myGeoTrend geoTrend, String sampleDataFile) {

        //int timer = 0 ;

        try {
            File file = new File(sampleDataFile) ;
            FileReader fileReader = new FileReader(file) ;
            BufferedReader bufferedReader = new BufferedReader(fileReader) ;

            String line ;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lineList = Arrays.asList(line.split(",")) ;
                for (String s : lineList) {
                    System.out.print(s + " <> ") ;
                }
                System.out.println() ;

                String type = lineList.get(1) ;

                myPolygon newPolygon ;
                String keyword ;
                if (type.equals("point")) {
                    newPolygon = new myPolygon((new mbr(Double.parseDouble(lineList.get(2)), 0, 0, Double.parseDouble(lineList.get(3)))),0) ;
                    keyword = (lineList.get(4)).substring(1) ;
                }
                else if (type.equals("polygon")) {
                    newPolygon = new myPolygon((new mbr(Double.parseDouble(lineList.get(2)), Double.parseDouble(lineList.get(4)), Double.parseDouble(lineList.get(3)), Double.parseDouble(lineList.get(5)))),1) ;
                    keyword = (lineList.get(6)).substring(1) ;
                }
                else {
                    System.out.println("STRANGE TYPE");
                    continue;
                }
                long timestamp = Long.parseLong(lineList.get(0)) ;

                System.out.println(type + " - " + timestamp + " - " + keyword + " - " +
                        newPolygon.mbrPolygon.leftUp.longitude + " - " + newPolygon.mbrPolygon.leftUp.latitude + " - " + newPolygon.mbrPolygon.rightDown.longitude + " - " + newPolygon.mbrPolygon.rightDown.latitude);

                geoTrend.newKeyword(keyword, newPolygon, timestamp) ;
            }
            fileReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
