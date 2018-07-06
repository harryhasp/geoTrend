package fixedShapingUncertain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    void insertIndexingKeywords(String sampleDataFile) {

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

                    firstCell.addIndexingKeyword(keyword, newPolygon) ;
                }

//                String keyword = (lineList.get(3)).substring(1) ;
//                //double longitude = Double.parseDouble(lineList.get(1)) ;
//                //double latitude = Double.parseDouble(lineList.get(2)) ;
//                myPoint newPoint = new myPoint(Double.parseDouble(lineList.get(1)), Double.parseDouble(lineList.get(2))) ;
//                //long timestamp = Long.parseLong(lineList.get(0)) ;
//
//                firstCell.addIndexingKeyword(keyword, newPoint) ;
            }
            fileReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        clearIndex() ;
    }


//    void newKeywordForIndexing (String keyword, myPoint point) {
//        int ret ;
//
//        firstCell.addIndexingKeyword(keyword, point) ;
//        //ret = firstCell.addIndexingKeyword(keyword, point) ;
//
//        //System.out.println("ret = " + ret) ;
//
//        System.out.println("-----------------------------------------------------------");
//    }


    void clearIndex () {
        firstCell.clearCellData() ;
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


//                i++ ;
//                myTimestamp = i / 10 ;
//                System.out.println("myTimestamp = " + myTimestamp) ;
//                long timestamp = Long.parseLong(lineList.get(0)) ;

                p = (int) ( (timestamp / (T/N)) % N) ;
                p = N - 1 - p ;
                System.out.println("-------------> p = " + p);

                //firstCell.addKeyword(keyword, newPoint, myTimestamp, p) ;
                firstCell.addKeyword(keyword, newPolygon, timestamp, p) ;
            }
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<Integer> levels = new HashSet<>() ;
        int insertions = statistics(levels);
        System.out.println("My statistics: Levels = " + levels.size() + " , Insertions = " + insertions);
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
//    }


    int statistics(Set<Integer> levels) {
        int ret = this.firstCell.statistics(levels) ;
        return ret ;
    }

}
