package accurateReplicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class insertKeywordsClass {

    void insertKeywords(myGeoTrend geoTrend, String sampleDataFile) {

        //int timer = 0 ;

        try {
//            Runtime rt = Runtime.getRuntime();
//            long prevFree = rt.freeMemory();
//            List<Long> mytest = new LinkedList<>();

            File file = new File(sampleDataFile) ;
            FileReader fileReader = new FileReader(file) ;
            BufferedReader bufferedReader = new BufferedReader(fileReader) ;

            String line ;
            long i = 0 ;
            long myTimestamp = 0 ;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lineList = Arrays.asList(line.split(",")) ;
                for (String s : lineList) {
                    System.out.print(s + " <> ") ;
                }
                System.out.println() ;

                String keyword = (lineList.get(3)).substring(1) ;
                //double longitude = Double.parseDouble(lineList.get(1)) ;
                //double latitude = Double.parseDouble(lineList.get(2)) ;
                myPoint newPoint = new myPoint(Double.parseDouble(lineList.get(1)), Double.parseDouble(lineList.get(2))) ;

                i++ ;
                myTimestamp = i / 10 ;
                System.out.println("myTimestamp = " + myTimestamp) ;
                long timestamp = Long.parseLong(lineList.get(0)) ;

                geoTrend.newKeyword(keyword, newPoint, myTimestamp) ;
                //geoTrend.newKeyword(keyword, newPoint, timestamp) ;

                //System.in.read() ;

//                long total = rt.totalMemory();
//                long free = rt.freeMemory();
//                i++ ;
//                if (i % 10 == 0) {
//                System.out.println(String.format("-----------> #%s, Total: %s, Free: %s, Diff: %s", i, total, free, total - free));
//                    if (prevFree > total-free) {
//                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% : " + i);
//                        System.in.read() ;
//                    }
//                    prevFree = total - free;
//                    mytest.add(total-free) ;
//                }
            }
            fileReader.close();
//            for (int j = 0 ; j < mytest.size() ; j++) {
//                System.out.println((j+1) + " - " + mytest.get(j));
//            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Set<Integer> levels = new HashSet<>() ;
        int insertions = geoTrend.statistics(levels);
        System.out.println("My statistics: Levels = " + levels.size() + " , Insertions = " + insertions);
    }

}
