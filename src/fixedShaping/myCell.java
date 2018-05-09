package fixedShaping;

import java.util.Hashtable;
import java.util.Set;

public class myCell {

    double maxCapacity ;
    double curCapacity ;
    int p ;
    mbr mbr ;
    Hashtable<String, hashValue> hashC ;
    int level ;
    myCell leftUpCell, leftDownCell, rightUpCell, rightDownCell;
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
        this.leftUpCell = this.leftDownCell = this.rightUpCell = this.rightDownCell = null ;
        this.k = k ;
        this.N = N ;
        this.T = T ;
        this.p = 0 ;
        this.lastTimestamp = 0 ;
    }

    // TO DO :
    int addIndexingKeyword (String keyword, myPoint point, double aboveCounter) {
/*
        if (this.leftUpCell == null) { // we are at a leaf - no children exist
            System.out.println("--> We are at a leaf - level = " + this.level);
            double toIncreaseCapacity ;
            if (aboveCounter == -1) {
                toIncreaseCapacity = 1 ;
            }
            else {
                toIncreaseCapacity = aboveCounter ;
            }
            if (this.curCapacity + toIncreaseCapacity <= this.maxCapacity) { // if we have space for one/aboveCounter more keywords
                System.out.println("--> We have space for one/aboveCounter more keywords");
                this.curCapacity = this.curCapacity + toIncreaseCapacity ;

                if (hashC.containsKey(keyword)) { // keyword already exists at this leaf
                    System.out.println("--> We have it already: " + keyword);
                    hashValue temp_hashValue = hashC.get(keyword);
                    if (aboveCounter == -1) { // keyword does NOT come from a split
                        temp_hashValue.countersN[p]++;
                    }
                    else { // keyword comes from a split
                        temp_hashValue.countersN[p] = aboveCounter ;
                    }
                    temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                    hashC.put(keyword, temp_hashValue);
                }
                else { // new keyword
                    System.out.println("--> We add '" + keyword + "' to level " + this.level);
                    hashValue temp_hashValue = new hashValue(this.N);
                    if (aboveCounter == -1) { // new keyword does NOT come from a split
                        temp_hashValue.countersN[p] = 1 ;
                    }
                    else { // new keyword comes from a split
                        temp_hashValue.countersN[p] = aboveCounter ;
                    }
                    temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                    hashC.put(keyword, temp_hashValue);
                }
            }
            else { // if we need to split this cell into 4 new
                System.out.println("--> We need to split this cell into 4 new");
                double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
                double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
                System.out.println("splits to: " + splitX + " - " + splitY);

                // create the 4 new cells
                System.out.println("--> We need to create leftUpCell");
                this.leftUpCell = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);
                System.out.println("--> We need to create rightUpCell");
                this.rightUpCell = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);
                System.out.println("--> We need to create leftDownCell");
                this.leftDownCell = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);
                System.out.println("--> We need to create rightDownCell");
                this.rightDownCell = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);

                // push old values to new cells
                Set<String> keys = hashC.keySet();
                int tempP ;
                double approximateCounter ;
                myPoint nullPoint = new myPoint(0,0) ;
                for (String key : keys) {
                    System.out.println("About to transfer keyword : " + key);
                    hashValue temp_hashValue = hashC.get(key);
                    for (int i = 0 ; i < this.N - 1 ; i++) {
                        tempP = (i+p) % this.N ;
                        approximateCounter = temp_hashValue.countersN[tempP] / 4 ;
                        System.out.println("Transfer down to leftUpCell the keyword : " + key + " --> " + approximateCounter);
                        (this.leftUpCell).addKeyword(key, nullPoint, timestamp, tempP, approximateCounter);
                        System.out.println("Transfer down to rightUpCell the keyword : " + key + " --> " + approximateCounter);
                        (this.rightUpCell).addKeyword(key, nullPoint, timestamp, tempP, approximateCounter);
                        System.out.println("Transfer down to leftDownCell the keyword : " + key + " --> " + approximateCounter);
                        (this.leftDownCell).addKeyword(key, nullPoint, timestamp, tempP, approximateCounter);
                        System.out.println("Transfer down to rightDownCell the keyword : " + key + " --> " + approximateCounter);
                        (this.rightDownCell).addKeyword(key, nullPoint, timestamp, tempP, approximateCounter);
                    }
                }

                // add new keyword to the aggregate table
                if (hashC.containsKey(keyword)) { // existed keyword
                    System.out.println("--> We have it already: " + keyword);
                    hashValue temp_hashValue = hashC.get(keyword);
                    if (aboveCounter == -1) { // keyword does NOT come from a split
                        System.out.println("-----------------------------------> H E R E - 1");
                        temp_hashValue.countersN[p]++;
                    }
                    else { // keyword comes from a split - probably never come here
                        System.out.println("-----------------------------------> H E R E - 2");
                        temp_hashValue.countersN[p] = aboveCounter ;
                    }
                    //temp_hashValue.countersN[p]++;
                    temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                    hashC.put(keyword, temp_hashValue);
                }
                else { // new keyword
                    System.out.println("--> We add '" + keyword + "' to level " + this.level);
                    hashValue temp_hashValue = new hashValue(this.N);
                    if (aboveCounter == -1) { // new keyword does NOT come from a split
                        System.out.println("-----------------------------------> H E R E - 3");
                        temp_hashValue.countersN[p] = 1 ;
                    }
                    else { // new keyword comes from a split - probably never come here
                        System.out.println("-----------------------------------> H E R E - 4");
                        temp_hashValue.countersN[p] = aboveCounter ;
                    }
                    //temp_hashValue.countersN[p] = 1 ;
                    temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                    hashC.put(keyword, temp_hashValue);
                }

                // push keyword to the appropriate child
                System.out.println("--> We need to go down to level = " + (this.level+1));
                //System.out.println(point.longitude + " - " + point.latitude);

                // choose the correct child
                if ((point.longitude < splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 0");
                    (this.leftUpCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
                }
                else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 1");
                    (this.rightUpCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
                }
                else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 2");
                    (this.leftDownCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
                }
                else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 3");
                    (this.rightDownCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
                }
                else {
                    System.out.println("----> PROBLEM: SOMETHING STRANGE IS GOING ON");
                }
            }

        }
        else { // we are NOT at a leaf - all the 4 children exist
            System.out.println("--> We are NOT at a leaf - level = " + this.level);

            // add keyword to the aggregate table
            if (hashC.containsKey(keyword)) { // existed keyword
                System.out.println("--> We have it already: " + keyword);
                hashValue temp_hashValue = hashC.get(keyword);
                if (aboveCounter == -1) { // keyword does NOT come from a split
                    System.out.println("-----------------------------------> H E R E - 1.1");
                    temp_hashValue.countersN[p]++;
                }
                else { // keyword comes from a split - probably never come here
                    System.out.println("-----------------------------------> H E R E - 2.1");
                    temp_hashValue.countersN[p] = aboveCounter ;
                }
                //temp_hashValue.countersN[p]++;
                temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                hashC.put(keyword, temp_hashValue);
            }
            else { // new keyword
                System.out.println("--> We add '" + keyword + "' to level " + this.level);
                hashValue temp_hashValue = new hashValue(this.N);
                if (aboveCounter == -1) { // new keyword does NOT come from a split
                    System.out.println("-----------------------------------> H E R E - 3.1");
                    temp_hashValue.countersN[p] = 1 ;
                }
                else { // new keyword comes from a split - probably never come here
                    System.out.println("-----------------------------------> H E R E - 4.1");
                    temp_hashValue.countersN[p] = aboveCounter ;
                }
                //temp_hashValue.countersN[p] = 1 ;
                temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                hashC.put(keyword, temp_hashValue);
            }

            // push keyword to the appropriate child
            System.out.println("--> We need to go down to level = " + (this.level+1));
            double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
            double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
            System.out.println(point.longitude + " - " + point.latitude);
            System.out.println("splits to: " + splitX + " - " + splitY);
            // choose the correct child
            if ((point.longitude < splitX) && (point.latitude >= splitY)) {
                System.out.println("--> cellCase = 0");
                (this.leftUpCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
            }
            else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                System.out.println("--> cellCase = 1");
                (this.rightUpCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
            }
            else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                System.out.println("--> cellCase = 2");
                (this.leftDownCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
            }
            else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                System.out.println("--> cellCase = 3");
                (this.rightDownCell).addKeyword(keyword, point, timestamp, p, aboveCounter);
            }
            else {
                System.out.println("----> PROBLEM: SOMETHING STRANGE IS GOING ON");
            }
        }

        printCell();
*/
        return 0;
    }


    double trendCalculation(int p, double[] countersN) {
        int down = N*(N+1)*(2*N+1) ;
        double up = 0.0 ;
        int cO = (p+N-1)%N ;
        int multiplier = N-1 ;
        for (int i = 0 ; i < this.N - 1 ; i++) {
            int tempP = (i+p) % this.N ;
            up = up + (multiplier * (countersN[tempP] - countersN[cO])) ;
            multiplier-- ;
        }
        //System.out.println("--> retTrend : " + (double) (6*up) / down);
        return 6*up / down ;
    }


    void printCell() {
        System.out.println("Print for level " + this.level);
        Set<String> keys = hashC.keySet() ;
        for (String key: keys) {
            hashValue temp = hashC.get(key) ;
            System.out.println(key + " - Trend = " + temp.trend);
            for (int i = 0 ; i < temp.countersN.length ; i++) {
                System.out.println("counter = " + temp.countersN[i]);
            }
        }
    }

}
