package mySystem;

import java.io.* ;
import java.util.* ;

public class indexShapingClass {

    public void indexShaping(myGeoTrend geoTrend, String sampleDataFile) {

        try {
            File file = new File(sampleDataFile) ;
            FileReader fileReader = null ;
            fileReader = new FileReader(file) ;
            BufferedReader bufferedReader = new BufferedReader(fileReader) ;

            //Hashtable<String, hashValue> hashC = new Hashtable<>() ;

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
            /*
            // check hash data
            Set<String> keys = hashC.keySet();
            for(String key: keys){
                hashValue temp_hashValue = hashC.get(key) ;
                System.out.print("Value of " + key + " is: ") ;
                for (int i = 0 ; i < (temp_hashValue.countersN).length ; i++ ) {
                    System.out.print((temp_hashValue.countersN)[i] + " - " ) ;
                }
                System.out.println() ;
            }
            */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
