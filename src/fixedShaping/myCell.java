package fixedShaping;

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
        //this.e = e ;
        if (level == 0) {
            this.e = 0.01 ;
        }
        else if (level == 1) {
            this.e = 0.001 ;
        }
        else if (level == 2) {
            this.e = 0.0001 ;
        }
        else {
            this.e = 0.00001 ;
        }
        this.p = N-1 ;
        this.pExp = N-1 ;
        this.lastInsertTimestamp = 0 ;
        this.countersSum = new double[N] ;
        Arrays.fill(countersSum, 0.0);
        this.counterInsertion = 0 ;
        this.topKList = new LinkedList<>() ;
        this.lastExpirationTimestamp = 0 ;
    }

    // TO DO :
    void addIndexingKeyword (String keyword, myPoint point) {

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
                    this.leftUpCell = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T, this.e);
                    (this.leftUpCell).addIndexingKeyword(keyword, point);
                }
                else if ((point.longitude >= splitX) && (point.latitude >= splitY)) {
                    System.out.println("--> cellCase = 1");
                    System.out.println("--> We need to create rightUpCell");
                    this.rightUpCell = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T, this.e);
                    (this.rightUpCell).addIndexingKeyword(keyword, point);
                }
                else if ((point.longitude < splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 2");
                    System.out.println("--> We need to create leftDownCell");
                    this.leftDownCell = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T, this.e);
                    (this.leftDownCell).addIndexingKeyword(keyword, point);
                }
                else if ((point.longitude >= splitX) && (point.latitude < splitY)) {
                    System.out.println("--> cellCase = 3");
                    System.out.println("--> We need to create rightDownCell");
                    this.rightDownCell = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T, this.e);
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
                    this.leftUpCell = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T, this.e);
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
                    this.rightUpCell = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level + 1, this.k, this.N, this.T, this.e);
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
                    this.leftDownCell = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T, this.e);
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
                    this.rightDownCell = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level + 1, this.k, this.N, this.T, this.e);
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

        //return 0;
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


    void addKeyword (String keyword, myPoint point, long timestamp, int p) {

        //this.p = p ;

        lazyExpiration(timestamp) ;

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

        boolean toTrendMem = false ;
        double sumOfCountersSum = DoubleStream.of(this.countersSum).sum() ;
        if ((sumOfCountersSum % (1/this.e)) == 0) {
            System.out.println("-||-> Going to trendMem for level " + this.level + " - " + sumOfCountersSum + " - " + (1/this.e));
            toTrendMem = trendMem(sumOfCountersSum);
        }


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
            if (toTrendMem) {
                System.out.println("--> We update the topKList because of trendMem");
                topKList.clear();
                Set<String> keys = hashC.keySet() ;
                for (String key : keys) {
                    updateTopKList(new topKNode(key, hashC.get(key).trend));
                }
            }
            else if (hashC.containsKey(keyword)) {
                updateTopKList(new topKNode(keyword, hashC.get(keyword).trend));
            }
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
                temp_hashValue.countersN[newC] = 0 ;
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
        return 6*up / down ;
    }


    private void updateTopKList(topKNode newNode) {
        boolean found = false ;

        // we check if we have at the topKList the keyword
        for (topKNode t : topKList) {
            if ((t.keyword).equals(newNode.keyword)) {
                t.trendValue = newNode.trendValue ;
                found = true ;
                topKListSorting(); // sort topKList
                break;
            }
        }

        if ( (topKList.size() < k) && (!found) ) { // if do not have it and we have space for one more
            topKList.add(newNode) ;
            topKListSorting(); // sort topKList
        }
        else if ( (!found) && (newNode.trendValue > topKList.get(topKList.size()-1).trendValue) ) { // if do not have it and no empty space
            topKList.remove(topKList.size()-1) ; // remove smaller
            topKList.add(newNode) ;
            topKListSorting(); // sort topKList
        }
        //topKListSorting(); // sort topKList
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
        System.out.println("this.p = " + this.p);
        System.out.println("this.pExp = " + this.pExp);
    }


    int statistics(Set<Integer> levels) {
        System.out.println("For statistics at level " + this.level);
        levels.add(this.level) ;

        Set<String> x = this.hashC.keySet() ;
        for (String s : x) {
            System.out.println(s);
        }

        int ret = 0 ;
        if (this.leftUpCell != null) {
            ret = ret + this.leftUpCell.statistics(levels) ;
        }
        if (this.leftDownCell != null) {
            ret = ret + this.leftDownCell.statistics(levels) ;
        }
        if (this.rightDownCell != null) {
            ret = ret + this.rightDownCell.statistics(levels) ;
        }
        if (this.rightUpCell != null) {
            ret = ret + this.rightUpCell.statistics(levels) ;
        }
//        if (this.leftUpCell != null) {
//            return this.leftUpCell.statistics(levels) + this.leftDownCell.statistics(levels) +
//                    this.rightDownCell.statistics(levels) + this.rightUpCell.statistics(levels) + this.hashC.size() ;
//        }
        return ret + this.hashC.size() ;
    }

}
