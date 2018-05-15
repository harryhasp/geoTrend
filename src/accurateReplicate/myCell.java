package accurateReplicate;

import java.util.*;

class myCell {

    private int maxCapacity ;
    private int curCapacity ;
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
    private List<topKNode> topKList ;


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
        this.lastInsertTimestamp = 0 ;
        this.countersSum = new int[N] ;
        Arrays.fill(countersSum, 0);
        this.counterInsertion = 0 ;
        this.topKList = new LinkedList<>() ;
    }

    // TO DO : store timestamp, now zero everywhere
    int addKeyword (String keyword, myPoint point, long timestamp, int p) {

        //this.p = p ;

//        if ( ((timestamp - this.lastTimestamp) % this.T) > 0 ) {
//            System.out.println("-----------------> We need to delete") ;
//        }

        if (this.leftUpCell == null) { // we are at a leaf - no children exist
            System.out.println("--> We are at a leaf - level = " + this.level);
            if (this.curCapacity + 1 <= this.maxCapacity) { // if we have space for one more keyword
                System.out.println("--> We have space for one more keyword");
                this.curCapacity++ ;
                this.counterInsertion++ ;
                this.countersSum[p]++ ;

                if (hashC.containsKey(keyword)) { // keyword already exists at this leaf
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
                    temp_hashValue.countersN[p] = 1 ;
                    temp_hashValue.locations[p].add(point) ;
                    //temp_hashValue.trend = (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
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
                for (String key : keys) {
                    System.out.println("About to transfer keyword : " + key);
                    hashValue temp_hashValue = hashC.get(key);
                    for (int i = 0 ; i < this.N - 1 ; i++) {
                        int tempP = (i+p) % this.N ;
                        for (int j = 0; j < temp_hashValue.locations[tempP].size(); j++) {
                            if ((temp_hashValue.locations[tempP].get(j).longitude < splitX) && (temp_hashValue.locations[tempP].get(j).latitude >= splitY)) {
                                System.out.println("Transfer down to leftUpCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                (this.leftUpCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                            }
                            else if ((temp_hashValue.locations[tempP].get(j).longitude >= splitX) && (temp_hashValue.locations[tempP].get(j).latitude >= splitY)) {
                                System.out.println("Transfer down to rightUpCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                (this.rightUpCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                            }
                            else if ((temp_hashValue.locations[tempP].get(j).longitude < splitX) && (temp_hashValue.locations[tempP].get(j).latitude < splitY)) {
                                System.out.println("Transfer down to leftDownCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                (this.leftDownCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                            }
                            else if ((temp_hashValue.locations[tempP].get(j).longitude >= splitX) && (temp_hashValue.locations[tempP].get(j).latitude < splitY)) {
                                System.out.println("Transfer down to rightDownCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                (this.rightDownCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                            }
                            else {
                                System.out.println("PROBLEM: (not fit to any new cell) with keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                            }
                        }
                        temp_hashValue.locations[tempP].clear();
                        System.out.println("--> Size = " + temp_hashValue.locations[tempP].size()); // test - to Delete
                    }
                    temp_hashValue.locations[(N-1+p)%N].clear(); // delete locations of the oldest counter
                    System.out.println("--> Size = " + temp_hashValue.locations[(N-1+p)%N].size()); // test - to Delete
                    hashC.replace(key, temp_hashValue) ;
                }

                // increase variables for the new keyword which initially goes to the aggregate table
                this.counterInsertion++ ;
                this.countersSum[p]++ ;

                // add new keyword to the aggregate table
                if (hashC.containsKey(keyword)) { // existed keyword
                    System.out.println("--> We have it already: " + keyword);
                    hashValue temp_hashValue = hashC.get(keyword);
                    temp_hashValue.countersN[p]++;
                    //temp_hashValue.locations[p].add(point) ;
                    temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                    hashC.put(keyword, temp_hashValue);
                }
                else { // new keyword
                    System.out.println("--> We add '" + keyword + "' to level " + this.level);
                    hashValue temp_hashValue = new hashValue(this.N);
                    temp_hashValue.countersN[p] = 1 ;
                    //temp_hashValue.locations[p].add(point) ;
                    //temp_hashValue.trend = (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
                    hashC.put(keyword, temp_hashValue);
                }

                // push keyword to the appropriate child
                System.out.println("--> We need to go down to level = " + (this.level+1));
                //System.out.println(point.longitude + " - " + point.latitude);

                // choose the correct child
                if ((point.longitude < splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 0");
                    (this.leftUpCell).addKeyword(keyword, point, timestamp, p);
                }
                else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 1");
                    (this.rightUpCell).addKeyword(keyword, point, timestamp, p);
                }
                else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 2");
                    (this.leftDownCell).addKeyword(keyword, point, timestamp, p);
                }
                else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 3");
                    (this.rightDownCell).addKeyword(keyword, point, timestamp, p);
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
            this.countersSum[p]++ ;

            // add keyword to the aggregate table
            if (hashC.containsKey(keyword)) { // existed keyword
                System.out.println("--> We have it already: " + keyword);
                hashValue temp_hashValue = hashC.get(keyword);
                temp_hashValue.countersN[p]++;
                //temp_hashValue.locations[p].add(point) ;
                temp_hashValue.trend = trendCalculation(p, temp_hashValue.countersN) ;
                hashC.put(keyword, temp_hashValue);
            }
            else { // new keyword
                System.out.println("--> We add '" + keyword + "' to level " + this.level);
                hashValue temp_hashValue = new hashValue(this.N);
                temp_hashValue.countersN[p] = 1 ;
                //temp_hashValue.locations[p].add(point) ;
                //temp_hashValue.trend = (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
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
                (this.leftUpCell).addKeyword(keyword, point, timestamp, p);
            }
            else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                System.out.println("--> cellCase = 1");
                (this.rightUpCell).addKeyword(keyword, point, timestamp, p);
            }
            else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                System.out.println("--> cellCase = 2");
                (this.leftDownCell).addKeyword(keyword, point, timestamp, p);
            }
            else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                System.out.println("--> cellCase = 3");
                (this.rightDownCell).addKeyword(keyword, point, timestamp, p);
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
        this.lastInsertTimestamp = timestamp ;

        printCell();

        return 0;
    }


    private double trendCalculation(int p, int[] countersN) {
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
                for (int j = 0 ; j < temp.locations[i].size() ; j++) {
                    System.out.println(temp.locations[i].get(j).longitude + " - " + temp.locations[i].get(j).latitude);
                }
            }
        }
        System.out.println("##> Printing topK Trend table");
        int counter = 1 ;
        for (topKNode t : topKList) {
            System.out.println(counter + ". " + t.keyword + " - " + t.trendValue);
            counter++ ;
        }
    }

}
