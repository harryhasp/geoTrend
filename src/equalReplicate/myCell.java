package equalReplicate;

import java.util.Hashtable;
import java.util.Set;

public class myCell {

    int maxCapacity ;
    int curCapacity ;
    int p ;
    equalReplicate.mbr mbr ;
    Hashtable<String, hashValue> hashC ;
    int level ;
    myCell leftUp, leftDown, rightUp, rightDown ;
    int k ;
    int N ;
    int T ; // T time units - number of time units that we keep data
    long lastTimestamp ;

    myCell(double minX, double maxX, double minY, double maxY, int level, int k, int N, int T) {
        System.out.println("myCell init - level = " + level) ;
        this.maxCapacity = 4 ;
        this.curCapacity = 0 ;
        this.mbr = new mbr(minX, maxX, minY, maxY) ;
        this.hashC = new Hashtable<>() ;
        this.level = level ;
        this.leftUp = this.leftDown = this.rightUp = this.rightDown = null ;
        this.k = k ;
        this.N = N ;
        this.T = T ;
        this.p = 0 ;
        this.lastTimestamp = 0 ;
    }

    // TO DO : store timestamp, now zero everywhere
    int addKeyword (String keyword, myPoint point, long timestamp, int p) {

        this.p = p ;

        if ( ((timestamp - this.lastTimestamp) % this.T) > 0 ) {
            System.out.println("-----------------> We need to delete") ;
        }

        // check if we are about to exceed the capacity
        if (this.curCapacity + 1 <= this.maxCapacity) {
            this.curCapacity++ ;
            if (hashC.containsKey(keyword)) {
                System.out.println("--> We have it already: " + keyword);
                hashValue temp_hashValue = hashC.get(keyword);
                temp_hashValue.countersN[p]++;
                temp_hashValue.locations[p].add(point) ;
                temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                hashC.put(keyword, temp_hashValue);
            }
            else { // new keyword
                System.out.println("--> We add '" + keyword + "' to level " + this.level);
                hashValue temp_hashValue = new hashValue(this.N);
                temp_hashValue.locations[p].add(point) ;
                temp_hashValue.countersN[p] = 1 ;
                //temp_hashValue.trend = (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
                hashC.put(keyword, temp_hashValue);
            }
        }
        else {
            //if we have exceeded the capacity of this leaf
            System.out.println("--> We need to go down - level = " + this.level);
            double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
            double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
            int cellCase = -1;

            System.out.println(point.longitude + " - " + point.latitude);
            System.out.println("splits to: " + splitX + " - " + splitY);

            if ((point.longitude < splitX) && (point.latitude >= splitY)) {
                cellCase = 0;
                System.out.println("--> cellCase = 0");
                if (this.leftUp == null) {
                    System.out.println("--> We need to create leftUp");
                    this.leftUp = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);

                    Set<String> keys = hashC.keySet();
                    for (String key : keys) {
                        hashValue temp_hashValue = hashC.get(key);
                        for (int i = 0 ; i < this.N - 1 ; i++) {
                            int tempP = (i+p) % this.N ;
                            for (int j = 0; j < temp_hashValue.locations[tempP].size(); j++) {
                                if ((temp_hashValue.locations[tempP].get(j).longitude < splitX) && (temp_hashValue.locations[tempP].get(j).latitude >= splitY)) {
                                    System.out.println("Transfer down the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    (this.leftUp).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                }
                            }
                        }
                    }

                    (this.leftUp).addKeyword(keyword, point, timestamp, p);
                }
                else {
                    System.out.println("--> We HAVE the leftUp");
                    (this.leftUp).addKeyword(keyword, point, timestamp, p);
                }
            }
            else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                cellCase = 1;
                System.out.println("--> cellCase = 1");
                if (this.rightUp == null) {
                    System.out.println("--> We need to create rightUp");
                    this.rightUp = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);

                    Set<String> keys = hashC.keySet();
                    for (String key : keys) {
                        hashValue temp_hashValue = hashC.get(key);
                        for (int i = 0 ; i < this.N - 1 ; i++) {
                            int tempP = (i+p) % this.N ;
                            for (int j = 0; j < temp_hashValue.locations[tempP].size(); j++) {
                                if ((temp_hashValue.locations[tempP].get(j).longitude >= splitX) && (temp_hashValue.locations[tempP].get(j).latitude >= splitY)) {
                                    System.out.println("Transfer down the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    (this.rightUp).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                }
                            }
                        }
                    }

                    (this.rightUp).addKeyword(keyword, point, timestamp, p);
                }
                else {
                    System.out.println("--> We HAVE the rightUp");
                    (this.rightUp).addKeyword(keyword, point, timestamp, p);
                }
            }
            else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                cellCase = 2;
                System.out.println("--> cellCase = 2");
                if (this.leftDown == null) {
                    System.out.println("--> We need to create leftDown");
                    this.leftDown = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);

                    Set<String> keys = hashC.keySet();
                    for (String key : keys) {
                        hashValue temp_hashValue = hashC.get(key);
                        for (int i = 0 ; i < this.N - 1 ; i++) {
                            int tempP = (i+p) % this.N ;
                            for (int j = 0; j < temp_hashValue.locations[tempP].size(); j++) {
                                if ((temp_hashValue.locations[tempP].get(j).longitude < splitX) && (temp_hashValue.locations[tempP].get(j).latitude < splitY)) {
                                    System.out.println("Transfer down the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    (this.leftDown).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                }
                            }
                        }
                    }

                    (this.leftDown).addKeyword(keyword, point, timestamp, p);
                }
                else {
                    System.out.println("--> We HAVE the leftDown");
                    (this.leftDown).addKeyword(keyword, point, timestamp, p);
                }
            }
            else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                cellCase = 3;
                System.out.println("--> cellCase = 3");
                if (this.rightDown == null) {
                    System.out.println("--> We need to create rightDown");
                    this.rightDown = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);

                    Set<String> keys = hashC.keySet();
                    for (String key : keys) {
                        hashValue temp_hashValue = hashC.get(key);
                        for (int i = 0 ; i < this.N - 1 ; i++) {
                            int tempP = (i+p) % this.N ;
                            for (int j = 0; j < temp_hashValue.locations[tempP].size(); j++) {
                                if ((temp_hashValue.locations[tempP].get(j).longitude >= splitX) && (temp_hashValue.locations[tempP].get(j).latitude < splitY)) {
                                    System.out.println("Transfer down the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    (this.rightDown).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                }
                            }
                        }
                    }

                    (this.rightDown).addKeyword(keyword, point, timestamp, p);
                }
                else {
                    System.out.println("--> We HAVE the rightDown");
                    (this.rightDown).addKeyword(keyword, point, timestamp, p);
                }
            }
            else {
                System.out.println("----> SOMETHING STRANGE IS GOING ON");
            }

            return 1;

            /*
            //this.curCapacity++ ;
            if (this.curCapacity > this.maxCapacity) {
                System.out.println("--> We JUST exceeded the capacity") ;
                return 2 ;
            }
            else { // we still have space
                return 0 ;
            }
            */
        }

        printCell();

        return 0;
    }


    double trendCalculation(int p, int[] countersN) {
        int down = N*(N+1)*(2*N+1) ;
        int up = 0 ;
        int cO = (p+N-1)%N ;
        int multiplier = N-1 ;
        for (int i = 0 ; i < this.N - 1 ; i++) {
            int tempP = (i+p) % this.N ;
            up = up + (multiplier * (countersN[tempP] - countersN[cO])) ;
            multiplier-- ;
        }
        //System.out.println("--> retTrend : " + (double) (6*up) / down);
        return (double) (6*up) / down ;
    }


    void printCell() {
        Set<String> keys = hashC.keySet() ;
        for (String key: keys) {
            hashValue temp = hashC.get(key) ;
            System.out.println(key + " - Trend = " + temp.trend);
            for (int i = 0 ; i < temp.countersN.length ; i++) {
                System.out.println("counter = " + temp.countersN[i]);
                for (int j = 0 ; j < temp.locations[i].size() ; j++) {
                    System.out.println(temp.locations[i].get(j).longitude + " - " + temp.locations[i].get(j).latitude);
                }
            }
        }
    }

}