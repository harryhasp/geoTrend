package approximateReplicate;

import java.util.*;

class myCell {

    private double maxCapacity ;
    private double curCapacity ;
    private int p ;
    private int pExp ;
    private mbr mbr ;
    private Hashtable<String, hashValue> hashC ;
    private int level ;
    private myCell leftUpCell, leftDownCell, rightUpCell, rightDownCell;
    private int k ;
    private int N ;
    private int T ; // T time units - number of time units that we keep data
    private long lastInsertTimestamp ;
    private double[] countersSum ;
    private int counterInsertion ;
    private List<topKNode> topKList ;
    private long lastExpirationTimestamp ;


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
        this.p = N-1 ;
        this.pExp = N-1 ;
        this.lastInsertTimestamp = 0 ;
        this.countersSum = new double[N] ;
        Arrays.fill(countersSum, 0);
        this.counterInsertion = 0 ;
        this.topKList = new LinkedList<>() ;
        this.lastExpirationTimestamp = 0 ;
    }

    // TO DO : store timestamp, now zero everywhere
    int addKeyword (String keyword, myPoint point, long timestamp, int p, double aboveCounter) {

        //this.p = p ;

//        if ( ((timestamp - this.lastTimestamp) % this.T) > 0 ) {
//            System.out.println("-----------------> We need to delete") ;
//        }

        lazyExpiration(timestamp) ;

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
                this.counterInsertion++ ;
                this.countersSum[p] = this.countersSum[p] + toIncreaseCapacity ;

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
                        if (temp_hashValue.countersN[tempP] > 0.0) {
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
                }

                // increase variables for the new keyword which initially goes to the aggregate table
                this.counterInsertion++ ;
                this.countersSum[p]++ ; // because we never go to 2 and 4 - in case we found that it goes, make it with if/else and increase accordingly

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

            // increase variables for the new keyword which initially goes to the aggregate table
            this.counterInsertion++ ;
            this.countersSum[p]++ ; // because we never go to 2.1 and 4.1 - in case we found that it goes, make it with if/else and increase accordingly

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

        // update Trend values if we are in a new p (not as value)
        if ( (this.p != p) || (timestamp - this.lastInsertTimestamp > this.T/this.N) ) {
            System.out.println("--> We update the Trend values");
            Set<String> keys = hashC.keySet() ;
            for (String key : keys) {
                hashValue temp_hashValue = hashC.get(key) ;
                temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                hashC.put(key, temp_hashValue) ;
            }
            topKList.clear();
            for (String key : keys) {
                updateTopKList(new topKNode(key, hashC.get(key).trend));
            }
        }
        else {
            updateTopKList(new topKNode(keyword, hashC.get(keyword).trend));
        }
        this.p = p ;
        //this.pExp = p ;
        this.lastInsertTimestamp = timestamp ;

        printCell();

        return 0;
    }


    private void lazyExpiration(long timestamp) {
        System.out.println("&&> We are at lazyExpiration for level " + this.level + " with this.pExp " + this.pExp);
        int nc = (int) Math.floor((timestamp - this.lastExpirationTimestamp)/(T/N)) ;
        System.out.println("nc = " + nc);

        if (nc > N) {
            nc = N ;
        }

        for (int i = 0 ; i < nc ; i++) {
            System.out.println("&&> Inside the for loop");
            int newC = --this.pExp ;
            System.out.println("init newC = " + newC);
            if (newC == -1) {
                newC = N - 1 ;
                this.pExp = N - 1 ;
            }
            else if (newC < -1) {
                System.out.println("--> PROBLEM on lazyExpiration");
            }
            System.out.println("for newC = " + newC);
            Set<String> keys = hashC.keySet() ;
            for (String key : keys) {
                hashValue temp_hashValue = hashC.get(key) ;
                temp_hashValue.countersN[newC] = 0 ;
                hashC.put(key, temp_hashValue) ;
            }
            this.countersSum[newC] = 0 ;
            //this.pExp-- ;
        }
        System.out.println("eventually this.pExp = " + this.pExp);

        this.lastExpirationTimestamp = this.lastExpirationTimestamp + nc * (T/N) ;

        System.out.println("Print for LAZY");
        printCell();
        System.out.println("- - -");
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


    private void updateTopKList(topKNode newNode) {
        if (topKList.size() < k) {
            topKList.add(newNode) ;
        }
        else {
            boolean found = false ;
            for (topKNode t : topKList) {
                if ((t.keyword).equals(newNode.keyword)) {
                    t.trendValue = newNode.trendValue ;
                    found = true ;
                }
            }
            if (!found) {
                topKList.remove(topKList.size()-1) ;
                topKList.add(newNode) ;
            }
        }
        topKListSorting();
    }

    private void topKListSorting () {
        topKList.sort(new Comparator<topKNode>() {
            @Override
            public int compare(topKNode o1, topKNode o2) {
                if(o1.trendValue < o2.trendValue){
                    return 1;
                } else if (o1.trendValue > o2.trendValue) {
                    return -1;
                } else {
                    return 0 ;
                }
            }
        });
    }


    private void printCell() {
        System.out.println("@@> Print for level " + this.level);
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
        System.out.println("##> Printing topK Trend table");
        int counter = 1 ;
        for (topKNode t : topKList) {
            System.out.println(counter + ". " + t.keyword + " - " + t.trendValue);
            counter++ ;
        }
        System.out.println("this.pExp = " + this.pExp);
    }

}
