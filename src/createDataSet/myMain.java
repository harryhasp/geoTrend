package createDataSet;

import java.io.* ;
import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom ;

public class myMain {

    public static void main(String[] args) throws IOException {
        System.out.println("Let's start") ;

        String[] myWords = {"#apple", "#big", "#cat", "#dog", "#bus", "#lion", "#microsoft", "#tinder", "#amazon",
                "#intel", "#hadoop", "#spark", "#splunk", "#olympiakos", "#legend", "#gate7", "#osfp", "#printezis",
                "#alba", "#asus"} ;
        double leftLimit = -180.0 ;
        double rightLimit = 180.0 ;
        double downLimit = -90.0 ;
        double upLimit = 90.0 ;


        // Create the sample file for initial index sharping
        //File fout1 = new File("C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\sampleData.txt") ;
        File fout1 = new File("C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\smallData.txt") ;
        FileOutputStream fos1 = new FileOutputStream(fout1) ;

        BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(fos1));

        for (int i = 0 ; i < 10 ; i++) {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            //System.out.println(timestamp.getTime());
            bw1.write(Long.toString(timestamp.getTime()) + ",") ;

            double x = ThreadLocalRandom.current().nextDouble(leftLimit, rightLimit) ;
            //double x = ThreadLocalRandom.current().nextDouble(rightLimit - leftLimit) + leftLimit ;
            if ( (x < leftLimit) || (x > rightLimit) ) {
                System.out.println("Found x = " + x) ;
            }
            else {
                bw1.write(Double.toString(x) + ",") ;
            }

            double y = ThreadLocalRandom.current().nextDouble(downLimit, upLimit) ;
            //System.out.println("y = " + y) ;
            if ( (y < downLimit) || (y > upLimit) ) {
                System.out.println("Found y = " + y) ;
            }
            else {
                bw1.write(Double.toString(y) + ",") ;
            }

            int tempWordNum = ThreadLocalRandom.current().nextInt(0, 19 + 1);
            if ( (tempWordNum < 0) || (tempWordNum >= 20) ) {
                System.out.println("Found tempWordNum = " + tempWordNum) ;
            }
            else {
                //System.out.println("--> " + myWords[tempWordNum]);
                bw1.write(myWords[tempWordNum]) ;
            }

            bw1.newLine();
        }

        bw1.close();

        /*
        // Create our real data
        File fout2 = new File("C:\\Users\\sir7o\\IdeaProjects\\geoTrend\\src\\newData.txt") ;
        FileOutputStream fos2 = new FileOutputStream(fout2) ;

        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(fos2));

        for (int i = 0 ; i < 10000 ; i++) {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            //System.out.println(timestamp.getTime());
            bw2.write(Long.toString(timestamp.getTime()) + ",") ;

            double x = ThreadLocalRandom.current().nextDouble(leftLimit, rightLimit) ;
            //double x = ThreadLocalRandom.current().nextDouble(rightLimit - leftLimit) + leftLimit ;
            if ( (x < leftLimit) || (x > rightLimit) ) {
                System.out.println("Found x = " + x) ;
            }
            else {
                bw2.write(Double.toString(x) + ",") ;
            }
            double y = ThreadLocalRandom.current().nextDouble(downLimit, upLimit) ;
            //System.out.println("y = " + y) ;
            if ( (y < downLimit) || (y > upLimit) ) {
                System.out.println("Found y = " + y) ;
            }
            else {
                bw2.write(Double.toString(y) + ",") ;
            }

            int tempWordNum = ThreadLocalRandom.current().nextInt(0, 19 + 1);
            if ( (tempWordNum < 0) || (tempWordNum >= 20) ) {
                System.out.println("Found tempWordNum = " + tempWordNum) ;
            }
            else {
                //System.out.println("--> " + myWords[tempWordNum]);
                bw2.write(myWords[tempWordNum]) ;
            }

            bw2.newLine();
        }

        bw2.close();
        */

        System.out.println("Finish") ;
    }

}
