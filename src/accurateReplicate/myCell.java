package accurateReplicate;

import java.util.*;
import java.util.stream.DoubleStream;

class myCell {

    private double maxCapacity ;
    private double curCapacity ;
    private int p ;
    private int pExp ;
    private mbr mbr ;
    private HashMap<String, hashValue> hashC ;
    private int level ;
    private myCell leftUpCell, leftDownCell, rightUpCell, rightDownCell;
    private int k ;
    private int N ;
    private int T ; // T time units - number of time units that we keep data
    private double e ;
    private long lastInsertTimestamp ;
    private double[] countersSum ;
    private int counterInsertion ;
    private List<topKNode> topKList ;
    private long lastExpirationTimestamp ;
    private int maxLevel ;


    myCell(double minX, double maxX, double minY, double maxY, int level, int k, int N, int T, double e) {
        System.out.println("myCell init - level = " + level) ;
        this.maxCapacity = 4 ;
        this.curCapacity = 0 ;
        this.mbr = new mbr(minX, maxX, minY, maxY) ;
        this.hashC = new HashMap<>() ;
        this.level = level ;
        this.leftUpCell = this.leftDownCell = this.rightUpCell = this.rightDownCell = null ;
        this.k = k ;
        this.N = N ;
        this.T = T ;
        this.e = e ;
        this.p = N-1 ;
        this.pExp = N-1 ;
        this.lastInsertTimestamp = 0 ;
        this.countersSum = new double[N] ;
        Arrays.fill(countersSum, 0.0);
        this.counterInsertion = 0 ;
        this.topKList = new LinkedList<>() ;
        this.lastExpirationTimestamp = 0 ;
        this.maxLevel = 30 ;
    }


    private void addKeywordToMap(Map<String, hashValue> playingMap, String key, int pHere, myPoint point) {
        if (playingMap.containsKey(key)) { // keyword already exists at this leaf
            System.out.println("--> We have it already: " + key);
            hashValue temp_hashValue = playingMap.get(key);
            temp_hashValue.countersN[pHere]++;
            temp_hashValue.locations[pHere].add(point) ;
            temp_hashValue.trend = trendCalculation(pHere, temp_hashValue.countersN) ;
            playingMap.put(key, temp_hashValue);
        }
        else { // new keyword
            System.out.println("--> We add '" + key + "' for future level");
            hashValue temp_hashValue = new hashValue(this.N);
            temp_hashValue.countersN[pHere] = 1 ;
            temp_hashValue.locations[pHere].add(point) ;
            //temp_hashValue.trend = (6*(N-1)) / (N*(N+1)*(2*N+1)) ;
            playingMap.put(key, temp_hashValue);
        }
    }


    private void smallUpdateTopKList() {
        System.out.println("--> We update the Trend values at smallUpdateTopKList");
        Set<String> keys = this.hashC.keySet() ;
        for (String key : keys) {
            hashValue temp_hashValue = this.hashC.get(key) ;
            temp_hashValue.trend = trendCalculation(this.p, temp_hashValue.countersN) ;
            this.hashC.put(key, temp_hashValue) ;
        }
        this.topKList.clear();
        for (String key : keys) {
            updateTopKList(new topKNode(key, this.hashC.get(key).trend));
        }
    }


    private int savingStackOverflow(double leftUpCellCap, double rightUpCellCap, double leftDownCellCap, double rightDownCellCap) {
        int which = -1 ;

        if (leftUpCellCap + rightUpCellCap + leftDownCellCap == 0) {
            which = 3 ;
        }
        else if (leftUpCellCap + rightUpCellCap + rightDownCellCap == 0) {
            which = 2 ;
        }
        else if (leftUpCellCap + leftDownCellCap + rightDownCellCap == 0) {
            which = 1 ;
        }
        else if (rightUpCellCap + leftDownCellCap + rightDownCellCap == 0) {
            which = 0 ;
        }

        return which ;
    }


    private void removeLess(HashMap<String, hashValue> tempMap) {

        List<topKNode> localTopKList = new LinkedList<>() ;

        Set<String> keys = tempMap.keySet() ;
        for (String key : keys) {
            localTopKList.add(new topKNode(key, tempMap.get(key).trend));
        }

        localTopKList.sort(new Comparator<topKNode>() {
            @Override
            public int compare(topKNode o1, topKNode o2) {
                if(o1.trendValue > o2.trendValue){
                    return 1;
                } else if (o1.trendValue < o2.trendValue) {
                    return -1;
                } else {
                    return 0 ;
                }
            }
        });

        for (int i = 0 ; i < 10 ; i++) {
            tempMap.remove(localTopKList.get(i).keyword) ;
        }
    }


    void addKeyword (String keyword, myPoint point, long timestamp, int p) {

        lazyExpiration(timestamp) ;

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
                if (this.level < maxLevel) { //we can split more
                    System.out.println("--> We need to split this cell into 4 new");
                    double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2);
                    double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2);
                    System.out.println("splits to: " + splitX + " - " + splitY);

                    // create the 4 new cells
                    System.out.println("--> We need to create leftUpCell");
                    this.leftUpCell = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T, this.e);
                    this.leftUpCell.lazyExpiration(timestamp);
                    System.out.println("--> We need to create rightUpCell");
                    this.rightUpCell = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T, this.e);
                    this.rightUpCell.lazyExpiration(timestamp);
                    System.out.println("--> We need to create leftDownCell");
                    this.leftDownCell = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T, this.e);
                    this.leftDownCell.lazyExpiration(timestamp);
                    System.out.println("--> We need to create rightDownCell");
                    this.rightDownCell = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T, this.e);
                    this.rightDownCell.lazyExpiration(timestamp);

                    HashMap<String, hashValue> forLeftUp = new HashMap<>() ;
                    HashMap<String, hashValue> forRightUp = new HashMap<>() ;
                    HashMap<String, hashValue> forLeftDown = new HashMap<>() ;
                    HashMap<String, hashValue> forRightDown = new HashMap<>() ;

                    Set<String> keys = hashC.keySet();
                    for (String key : keys) {
                        System.out.println("About to transfer keyword : " + key);
                        hashValue temp_hashValue = hashC.get(key);
                        for (int i = 0 ; i < this.N ; i++) {
                            int tempP = (i+p) % this.N ;
                            for (int j = 0; j < temp_hashValue.locations[tempP].size(); j++) {
                                if ((temp_hashValue.locations[tempP].get(j).longitude < splitX) && (temp_hashValue.locations[tempP].get(j).latitude >= splitY)) {
                                    System.out.println("Transfer down to leftUpCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    //(this.leftUpCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                    addKeywordToMap(forLeftUp, key, tempP, temp_hashValue.locations[tempP].get(j));
                                    this.leftUpCell.curCapacity++ ;
                                    this.leftUpCell.counterInsertion++ ;
                                    this.leftUpCell.countersSum[tempP]++ ;
                                }
                                else if ((temp_hashValue.locations[tempP].get(j).longitude >= splitX) && (temp_hashValue.locations[tempP].get(j).latitude >= splitY)) {
                                    System.out.println("Transfer down to rightUpCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    //(this.rightUpCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                    addKeywordToMap(forRightUp, key, tempP, temp_hashValue.locations[tempP].get(j));
                                    this.rightUpCell.curCapacity++ ;
                                    this.rightUpCell.counterInsertion++ ;
                                    this.rightUpCell.countersSum[tempP]++ ;
                                }
                                else if ((temp_hashValue.locations[tempP].get(j).longitude < splitX) && (temp_hashValue.locations[tempP].get(j).latitude < splitY)) {
                                    System.out.println("Transfer down to leftDownCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    //(this.leftDownCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                    addKeywordToMap(forLeftDown, key, tempP, temp_hashValue.locations[tempP].get(j));
                                    this.leftDownCell.curCapacity++ ;
                                    this.leftDownCell.counterInsertion++ ;
                                    this.leftDownCell.countersSum[tempP]++ ;
                                }
                                else if ((temp_hashValue.locations[tempP].get(j).longitude >= splitX) && (temp_hashValue.locations[tempP].get(j).latitude < splitY)) {
                                    System.out.println("Transfer down to rightDownCell the keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                    //(this.rightDownCell).addKeyword(key, temp_hashValue.locations[tempP].get(j), timestamp, tempP);
                                    addKeywordToMap(forRightDown, key, tempP, temp_hashValue.locations[tempP].get(j));
                                    this.rightDownCell.curCapacity++ ;
                                    this.rightDownCell.counterInsertion++ ;
                                    this.rightDownCell.countersSum[tempP]++ ;
                                }
                                else {
                                    System.out.println("PROBLEM: (not fit to any new cell) with keyword : " + key + " --> " + temp_hashValue.locations[tempP].get(j).longitude + " - " + temp_hashValue.locations[tempP].get(j).latitude);
                                }
                            }
                            temp_hashValue.locations[tempP].clear();
                            System.out.println("--> Size 1 = " + temp_hashValue.locations[tempP].size()); // test - to Delete
                        }
                        //temp_hashValue.locations[(N-1+p)%N].clear(); // delete locations of the oldest counter
                        System.out.println("--> Size 2 = " + temp_hashValue.locations[(N-1+p)%N].size()); // test - to Delete
                        hashC.replace(key, temp_hashValue) ;
                    }

                    this.leftUpCell.p = this.rightUpCell.p = this.leftDownCell.p = this.rightDownCell.p = p ;
                    this.leftUpCell.lastInsertTimestamp = this.rightUpCell.lastInsertTimestamp = this.leftDownCell.lastInsertTimestamp = this.rightDownCell.lastInsertTimestamp = timestamp ;

//                    System.out.println(forLeftUp.size() + " - " + this.leftUpCell.curCapacity) ;
//                    int which = savingStackOverflow(this.leftUpCell.curCapacity, this.rightUpCell.curCapacity, this.leftDownCell.curCapacity, this.rightDownCell.curCapacity) ;
//                    if (which != -1) {
//                        if (which == 0) {
//                            removeLess(forLeftUp) ;
//                        }
//                        else if (which == 1) {
//                            removeLess(forRightUp) ;
//                        }
//                        else if (which == 2) {
//                            removeLess(forLeftDown) ;
//                        }
//                        else if (which == 3) {
//                            removeLess(forRightDown) ;
//                        }
//                    }

                    this.leftUpCell.hashC = forLeftUp ;
                    this.leftUpCell.smallUpdateTopKList();

                    this.rightUpCell.hashC = forRightUp ;
                    this.rightUpCell.smallUpdateTopKList();

                    this.leftDownCell.hashC = forLeftDown ;
                    this.leftDownCell.smallUpdateTopKList();

                    this.rightDownCell.hashC = forRightDown ;
                    this.rightDownCell.smallUpdateTopKList();


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
                else { // no more down
                    this.counterInsertion++ ;
                    this.countersSum[p]++ ;
                    this.curCapacity++ ;

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


        boolean toTrendMem = false ;
        double sumOfCountersSum = DoubleStream.of(this.countersSum).sum() ;
//        double sumOfCountersSum = 0.0 ;
//        for (double s : this.countersSum) {
//            sumOfCountersSum = sumOfCountersSum + s ;
//        }
        if ((counterInsertion % (1/this.e)) == 0) {
            System.out.println("-||-> Going to trendMem for level " + this.level + " - " + counterInsertion + " - " + (1/this.e));
            toTrendMem = trendMem(sumOfCountersSum);
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
            if (hashC.containsKey(keyword)) {
                updateTopKList(new topKNode(keyword, hashC.get(keyword).trend));
            }
//            if (toTrendMem) {
//                System.out.println("--> We update the topKList because of trendMem");
//                topKList.clear();
//                Set<String> keys = hashC.keySet() ;
//                for (String key : keys) {
//                    updateTopKList(new topKNode(key, hashC.get(key).trend));
//                }
//            }
//            else if (hashC.containsKey(keyword)) {
//                updateTopKList(new topKNode(keyword, hashC.get(keyword).trend));
//            }
        }
        this.p = p ;
        //this.pExp = p ;
        this.lastInsertTimestamp = timestamp ;

        printCell();

        //return 0;
    }


    private void lazyExpiration(long timestamp) {
        System.out.println("&&> We are at lazyExpiration for level " + this.level + " with this.pExp " + this.pExp);
        int nc = (int) Math.floor((timestamp - this.lastExpirationTimestamp)/(T/N)) ;
        System.out.println("nc = " + nc);

        if (nc < 1) {
            System.out.println("No need to continue the lazyExpiration");
            return;
        }

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
                temp_hashValue.locations[newC].clear();
                if ( (this.leftUpCell == null) && (temp_hashValue.countersN[newC] > 0) ) {
                    System.out.println("&&> Leaf, so decrease current capacity");
                    this.curCapacity = this.curCapacity - temp_hashValue.countersN[newC] ;
                }
                temp_hashValue.countersN[newC] = 0.0 ;
                temp_hashValue.trend = trendCalculation(newC, temp_hashValue.countersN) ;
                hashC.put(key, temp_hashValue) ;
            }
            this.countersSum[newC] = 0 ;
            //this.pExp-- ;
        }

        // remove entries that have all counters Zero
        Set<String> keysOld = hashC.keySet() ;
        Iterator<String> it = keysOld.iterator() ;
        while (it.hasNext()) {
            String key = it.next() ;
            hashValue temp_hashValue = hashC.get(key) ;
            if (DoubleStream.of(temp_hashValue.countersN).sum() == 0.0) {
                System.out.println("-------------> All zero for " + key);
                it.remove();
            }
        }

        topKList.clear();
        Set<String> keysNew = hashC.keySet() ;
        for (String key : keysNew) {
            updateTopKList(new topKNode(key, hashC.get(key).trend));
        }
        System.out.println("eventually this.pExp = " + this.pExp);

        this.lastExpirationTimestamp = this.lastExpirationTimestamp + nc * (T/N) ;
        System.out.println("--> eventually this.lastExpirationTimestamp = " + this.lastExpirationTimestamp);

        System.out.println("Print for LAZY");
        printCell();
        System.out.println("- - -");
    }


    private boolean trendMem(double sumOfCountersSum) {
        boolean ret = false ;
        int counter ;
        Set<String> keys = hashC.keySet() ;
        Iterator<String> it = keys.iterator() ;
        hashValue temp_hashValue ;
        while (it.hasNext()) {
            String key = it.next() ;
            temp_hashValue = hashC.get(key) ;
            counter = 0 ;
            for (int i = 0 ; i < N ; i++) {
                if (temp_hashValue.countersN[i] < e*sumOfCountersSum) {
                    counter++ ;
                }
            }
            if (counter == N) {
                System.out.println("-------------> Because of trendMem remove " + key);
                for (int i = 0 ; i < N ; i++) {
                    this.countersSum[i] = this.countersSum[i] - temp_hashValue.countersN[i] ;
                }
                it.remove();
                ret = true ;
            }
        }

        return ret ;
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
        return (6*up) / down ;
    }


    private void updateTopKList(topKNode newNode) {
        boolean found = false ;

        // we check if we have at the topKList the keyword
//        for (topKNode t : topKList) {
//            if ((t.keyword).equals(newNode.keyword)) {
//                t.trendValue = newNode.trendValue ;
//                found = true ;
//            }
//        }

        if (topKList.contains(newNode.keyword)) {
            topKList.set(topKList.indexOf(newNode.keyword), newNode) ;
            found = true ;
        }

        if ( (topKList.size() < k) && (!found) ) {  // if do not have it and we have space for one more
            topKList.add(newNode) ;
        }
        else if (!found) { // if do not have it and no empty space
            topKList.remove(topKList.size()-1) ; // remove smaller
            topKList.add(newNode) ;
        }
        topKListSorting(); // sort topKList
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
        System.out.println("curCapacity = " + this.curCapacity);
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
        System.out.println("this.p = " + this.p);
        System.out.println("this.pExp = " + this.pExp);
    }


    int statistics(Set<Integer> levels, List<Integer> cells) {
        System.out.println("For statistics at level " + this.level);
        levels.add(this.level) ;

        cells.set(0, cells.get(0)+1) ;

        Set<String> x = this.hashC.keySet() ;
        for (String s : x) {
            System.out.println(s);
        }

        if (this.leftUpCell != null) {
            return this.leftUpCell.statistics(levels, cells) + this.leftDownCell.statistics(levels, cells) +
                    this.rightDownCell.statistics(levels, cells) + this.rightUpCell.statistics(levels, cells) + this.hashC.size() ;
        }
        return this.hashC.size() ;
    }

}
