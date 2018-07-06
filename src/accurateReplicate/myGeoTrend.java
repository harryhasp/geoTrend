package accurateReplicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class myGeoTrend {

    //private int depth ;
    private myCell firstCell ;
    //private int k ;
    private int N ; // N counters - T/N
    private int T ; // T time units - number of time units that we keep data
    private double e ;
    private int p ; // pointer


    myGeoTrend(int k, int N, int T, double e) {
        //this.depth = 1 ;
        this.firstCell = new myCell(-180.0, 180.0, -90.0, 90.0, 0, k, N, T, e) ;
        //this.k = k ;
        this.N = N ;
        this.T = T ;
        this.e = e ;
        this.p = 0 ;
    }


    void insertKeywords(String sampleDataFile) {

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

//                i++ ;
//                myTimestamp = i / 10 ;
//                System.out.println("myTimestamp = " + myTimestamp) ;
                long timestamp = Long.parseLong(lineList.get(0)) ;

                p = (int) ( (timestamp / (T/N)) % N) ;
                p = N - 1 - p ;
                System.out.println("-------------> p = " + p);

//                this.firstCell.addKeyword(keyword, newPoint, myTimestamp, p);
                this.firstCell.addKeyword(keyword, newPoint, timestamp, p);

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
        List<Integer> cells = new ArrayList<>() ;
        cells.add(0) ;
        int insertions = statistics(levels, cells);
        System.out.println("My statistics: Levels = " + levels.size() + " , Cells = " + cells.get(0) + " , Insertions = " + insertions);
    }


//    void newKeyword (String keyword, myPoint point, long timestamp) {
//        int ret ;
//
//        p = (int) ( (timestamp / (T/N)) % N) ;
//        p = N - 1 - p ;
//        System.out.println("-------------> p = " + p);
//
//        firstCell.addKeyword(keyword, point, timestamp, p) ;
//        //ret = firstCell.addKeyword(keyword, point, timestamp, p) ;
//
//        //System.out.println("ret = " + ret) ;
//
//        System.out.println("-----------------------------------------------------------");
//
////        System.out.println("----------------------------------------- printCell - start");
////        firstCell.printCell() ;
////        System.out.println("----------------------------------------- printCell - end");
//    }

    int statistics(Set<Integer> levels, List<Integer> cells) {
        int ret = this.firstCell.statistics(levels, cells) ;
        return ret ;
    }

}
