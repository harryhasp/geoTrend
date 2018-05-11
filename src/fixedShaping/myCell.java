package fixedShaping;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

class myCell {

    private double maxCapacity ;
    private double curCapacity ;
    private int p ;
    private mbr mbr ;
    private Hashtable<String, hashValue> hashC ;
    private int level ;
    private myCell leftUpCell, leftDownCell, rightUpCell, rightDownCell;
    private int k ;
    private int N ;
    private int T ; // T time units - number of time units that we keep data
    private long lastInsertTimestamp ;
    private int[] countersSum ;
    private int counterInsertion ;

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
        this.lastInsertTimestamp = 0 ;
        this.countersSum = new int[N] ;
        Arrays.fill(countersSum, 0);
        this.counterInsertion = 0 ;
    }

    // TO DO :
    int addIndexingKeyword (String keyword, myPoint point) {

        if ( (this.leftUpCell == null) && (this.rightUpCell == null) && (this.leftDownCell == null) && (this.rightDownCell == null) ) { // we are at a leaf - no children exist
            System.out.println("--> We are at a leaf - level = " + this.level);

            if (this.curCapacity + 1 <= this.maxCapacity) { // if we have space for one more keyword
                System.out.println("--> We have space for one more keyword");
                this.curCapacity++ ;
                this.counterInsertion++ ;

                if (hashC.containsKey(keyword)) { // keyword already exists at this leaf
                    System.out.println("--> We have it already: " + keyword);
                    hashValue temp_hashValue = hashC.get(keyword);
                    temp_hashValue.countersN[0]++;
                    hashC.put(keyword, temp_hashValue);
                }
                else { // new keyword
                    System.out.println("--> We add '" + keyword + "' to level " + this.level);
                    hashValue temp_hashValue = new hashValue(this.N);
                    temp_hashValue.countersN[0] = 1 ;
                    hashC.put(keyword, temp_hashValue);
                }
            }
            else { // if we need to split this cell into 4 new
                System.out.println("--> We need to split this cell into 4 new");
                double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
                double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
                System.out.println("splits to: " + splitX + " - " + splitY);

                // push keyword to the appropriate child
                System.out.println("--> We need to go down to level = " + (this.level+1));
                //System.out.println(point.longitude + " - " + point.latitude);

                // choose and create the correct child
                if ((point.longitude < splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 0");
                    System.out.println("--> We need to create leftUpCell");
                    this.leftUpCell = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);
                    (this.leftUpCell).addIndexingKeyword(keyword, point);
                }
                else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 1");
                    System.out.println("--> We need to create rightUpCell");
                    this.rightUpCell = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);
                    (this.rightUpCell).addIndexingKeyword(keyword, point);
                }
                else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 2");
                    System.out.println("--> We need to create leftDownCell");
                    this.leftDownCell = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);
                    (this.leftDownCell).addIndexingKeyword(keyword, point);
                }
                else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 3");
                    System.out.println("--> We need to create rightDownCell");
                    this.rightDownCell = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);
                    (this.rightDownCell).addIndexingKeyword(keyword, point);
                }
                else {
                    System.out.println("----> PROBLEM: SOMETHING STRANGE IS GOING ON");
                }
            }

        }
        else { // we are NOT at a leaf - any of the 4 children exist
            System.out.println("--> We are NOT at a leaf - level = " + this.level);

            // push keyword to the appropriate child
            System.out.println("--> We need to go down to level = " + (this.level+1));
            double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
            double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
            System.out.println(point.longitude + " - " + point.latitude);
            System.out.println("splits to: " + splitX + " - " + splitY);

            // choose and create the correct child
            if ((point.longitude < splitX) && (point.latitude >= splitY)) {
                System.out.println("--> cellCase = 0");
                if (this.leftUpCell == null) {
                    System.out.println("--> We need to create leftUpCell");
                    this.leftUpCell = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);
                }
                else { // to del
                    System.out.println("--> We have leftUpCell");
                }
                (this.leftUpCell).addIndexingKeyword(keyword, point);
            }
            else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                System.out.println("--> cellCase = 1");
                if (this.rightUpCell == null) {
                    System.out.println("--> We need to create rightUpCell");
                    this.rightUpCell = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T);
                }
                else { // to del
                    System.out.println("--> We have rightUpCell");
                }
                (this.rightUpCell).addIndexingKeyword(keyword, point);
            }
            else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                System.out.println("--> cellCase = 2");
                if (this.leftDownCell == null) {
                    System.out.println("--> We need to create leftDownCell");
                    this.leftDownCell = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);
                }
                else { // to del
                    System.out.println("--> We have leftDownCell");
                }
                (this.leftDownCell).addIndexingKeyword(keyword, point);
            }
            else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                System.out.println("--> cellCase = 3");
                if (this.rightDownCell == null) {
                    System.out.println("--> We need to create rightDownCell");
                    this.rightDownCell = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T);
                }
                else { // to del
                    System.out.println("--> We have rightDownCell");
                }
                (this.rightDownCell).addIndexingKeyword(keyword, point);
            }
            else {
                System.out.println("----> PROBLEM: SOMETHING STRANGE IS GOING ON");
            }
        }

        printCellIndexing();

        return 0;
    }


    void clearCellData () {
        System.out.println("Clear cell at level " + this.level + " with x from " + this.mbr.leftUp.longitude + " to " + this.mbr.rightDown.longitude +
                " and y from " + this.mbr.rightDown.latitude + " to " + this.mbr.leftUp.latitude) ;
        this.curCapacity = 0 ;
        this.hashC.clear();
        this.lastInsertTimestamp = 0 ;
        Arrays.fill(countersSum, 0);
        this.counterInsertion = 0 ;
        // go to children
        if (this.leftUpCell != null) {
            this.leftUpCell.clearCellData();
        }
        if (this.rightUpCell != null) {
            this.rightUpCell.clearCellData();
        }
        if (this.leftDownCell != null) {
            this.leftDownCell.clearCellData();
        }
        if (this.rightDownCell != null) {
            this.rightDownCell.clearCellData();
        }
        printCellIndexing();
    }


    int addKeyword (String keyword, myPoint point, long timestamp, int p) {

        //this.p = p ;

//        if ( ((timestamp - this.lastTimestamp) % this.T) > 0 ) {
//            System.out.println("-----------------> We need to delete") ;
//        }

        this.counterInsertion++ ;
        this.countersSum[p]++ ;

        if (hashC.containsKey(keyword)) { // existed keyword
            System.out.println("--> We have it already: " + keyword);
            hashValue temp_hashValue = hashC.get(keyword);
            temp_hashValue.countersN[p]++;
            temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
            hashC.put(keyword, temp_hashValue);
        }
        else { // new keyword
            System.out.println("--> We add '" + keyword + "' to level " + this.level);
            hashValue temp_hashValue = new hashValue(this.N);
            temp_hashValue.countersN[p] = 1 ;
            //temp_hashValue.trend = (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
            hashC.put(keyword, temp_hashValue);
        }

        // update Trend values if we are in a new p (not as value)
        if ( (this.p != p) || (timestamp - this.lastInsertTimestamp > this.T/this.N) ) {
            System.out.println("--> We update the Trend values");
            Set<String> keys = hashC.keySet() ;
            for (String key : keys) {
                hashValue temp_hashValue = hashC.get(key) ;
                temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                hashC.put(key, temp_hashValue) ;
            }
        }
        this.p = p ;

        // push keyword to the appropriate child if it exists
        double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
        double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
        System.out.println("splits to: " + splitX + " - " + splitY);

        if ((this.leftUpCell != null) && ((point.longitude < splitX) && (point.latitude >= splitY)) ) {
            System.out.println("--> cellCase = 0");
            (this.leftUpCell).addKeyword(keyword, point, timestamp, p);
        }
        else if ( (this.rightUpCell != null) && (point.longitude >= splitX) && (point.latitude >= splitY) ) {
            System.out.println("--> cellCase = 1");
            (this.rightUpCell).addKeyword(keyword, point, timestamp, p);
        }
        else if ( (this.leftDownCell != null) && (point.longitude < splitX) && (point.latitude < splitY) ) {
            System.out.println("--> cellCase = 2");
            (this.leftDownCell).addKeyword(keyword, point, timestamp, p);
        }
        else if ( (this.rightDownCell != null) && (point.longitude >= splitX) && (point.latitude < splitY) ) {
            System.out.println("--> cellCase = 3");
            (this.rightDownCell).addKeyword(keyword, point, timestamp, p);
        }
        else {
            System.out.println("----> PROBLEM: SOMETHING STRANGE IS GOING ON");
        }

        printCell();

        return 0;
    }


    private double trendCalculation(int p, double[] countersN) {
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


    private void printCellIndexing() {
        System.out.println("Print for level " + this.level);
        System.out.println("Total insertions = " + this.counterInsertion);
        Set<String> keys = hashC.keySet() ;
        for (String key: keys) {
            hashValue temp = hashC.get(key) ;
            System.out.println(key + " - Trend = " + temp.trend);
            for (int i = 0 ; i < temp.countersN.length ; i++) {
                System.out.println("counter = " + temp.countersN[i]);
            }
        }
    }


    private void printCell() {
        System.out.println("Print for level " + this.level);
        System.out.println("Total insertions = " + this.counterInsertion);
        for (int i = 0 ; i < this.countersSum.length ; i++) {
            System.out.println("countersSum at " + i + " = " + this.countersSum[i]);
        }
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
