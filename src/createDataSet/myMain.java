package createDataSet;

import java.io.* ;
import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom ;

public class myMain {

    public static void main(String[] args) throws IOException {
        System.out.println("Let's start") ;

        File fout = new File("C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\data.txt") ;
        FileOutputStream fos = new FileOutputStream(fout) ;

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        String[] myWords = {"#apple", "#big", "#cat", "#dog", "#bus", "#lion", "#microsoft", "#tinder", "#amazon",
                "#intel", "#hadoop", "#spark", "#splunk", "#olympiakos", "#legend", "#gate7", "#osfp", "#printezis",
                "#alba", "#asus"} ;

        for (int i = 0 ; i < 100 ; i++) {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            //System.out.println(timestamp.getTime());
            bw.write(Long.toString(timestamp.getTime()) + ",") ;

            double leftLimit = -180.0 ;
            double rightLimit = 180.0 ;
            double x = ThreadLocalRandom.current().nextDouble(leftLimit, rightLimit) ;
            //double x = ThreadLocalRandom.current().nextDouble(rightLimit - leftLimit) + leftLimit ;
            if ( (x < leftLimit) || (x > rightLimit) ) {
                System.out.println("Found x = " + x) ;
            }
            else {
                bw.write(Double.toString(x) + ",") ;
            }
            double upLimit = 90.0 ;
            double downLimit = -90.0 ;
            double y = ThreadLocalRandom.current().nextDouble(downLimit, upLimit) ;
            //System.out.println("y = " + y) ;
            if ( (y < downLimit) || (y > upLimit) ) {
                System.out.println("Found y = " + y) ;
            }
            else {
                bw.write(Double.toString(y) + ",") ;
            }

            int tempWordNum = ThreadLocalRandom.current().nextInt(0, 19 + 1);
            if ( (tempWordNum < 0) || (tempWordNum >= 20) ) {
                System.out.println("Found tempWordNum = " + tempWordNum) ;
            }
            else {
                //System.out.println("--> " + myWords[tempWordNum]);
                bw.write(myWords[tempWordNum]) ;
            }

            bw.newLine();
        }

        bw.close();

        System.out.println("Finish") ;
    }

}
