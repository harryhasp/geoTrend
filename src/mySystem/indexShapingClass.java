package mySystem;

import java.io.* ;
import java.util.* ;

public class indexShapingClass {

    public void indexShaping(String sampleDataFile) {
        try {
            File file = new File(sampleDataFile) ;
            FileReader fileReader = null ;
            fileReader = new FileReader(file) ;
            BufferedReader bufferedReader = new BufferedReader(fileReader) ;

            Hashtable<String, hashValue> hashC = new Hashtable<>() ;

            String line ;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lineList = Arrays.asList(line.split(",")) ;
                for (int i = 0 ; i < lineList.size() ; i++) {
                    System.out.println(lineList.get(i)) ;
                }

                String keyword = (lineList.get(3)).substring(1) ;

                //int value = hashC.containsKey(keyword) ? hashC.get(keyword) : 0 ;
                //hashC.put(keyword, value + 1) ;

                if (hashC.containsKey(keyword)) {
                    System.out.println("--> We have it already") ;
                    //hashValue temp_hashValue = hashC.get(keyword) ;
                    //temp_hashValue.counterN++ ;
                }
                else {
                    hashValue temp_hashValue = new hashValue() ;
                    hashC.put(keyword, temp_hashValue) ;
                }
            }
            fileReader.close();

            Set<String> keys = hashC.keySet();
            for(String key: keys){
                hashValue temp_hashValue = hashC.get(key) ;
                System.out.print("Value of " + key + " is: ") ;
                for (int i = 0 ; i < (temp_hashValue.countersN).length ; i++ ) {
                    System.out.print((temp_hashValue.countersN)[i] + " - " ) ;
                }
                System.out.println() ;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
