package mySystem;

import java.util.Hashtable;

public class myCell {

    int maxCapacity ;
    int curCapacity ;
    mbr mbr ;
    Hashtable<String, hashValue> hashC ;

    myCell(double minX, double maxX, double minY, double maxY) {
        System.out.println("myCell init") ;
        this.maxCapacity = 4 ;
        this.curCapacity = 0 ;
        this.mbr = new mbr(minX, maxX, minY, maxY) ;
        this.hashC = new Hashtable<>() ;
    }

    int addKeyword (String keyword) {
        if (hashC.containsKey(keyword)) {
            System.out.println("--> We have it already") ;
            hashValue temp_hashValue = hashC.get(keyword) ;
            (temp_hashValue.countersN)[0]++ ;
        }
        else {
            hashValue temp_hashValue = new hashValue() ;
            hashC.put(keyword, temp_hashValue) ;
        }

        if (this.curCapacity > this.maxCapacity) {  //
            System.out.println("--> We had already exceeded the capacity") ;
            this.curCapacity++ ;
            return 1 ;
        }
        else {
            this.curCapacity++ ;
            if (this.curCapacity > this.maxCapacity) {
                System.out.println("--> We JUST exceeded the capacity") ;
                return 2 ;
            }
            else { // we still have space
                return 0 ;
            }
        }
    }

}
