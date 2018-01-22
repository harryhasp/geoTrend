package mySystem;

import java.util.Hashtable;

public class myCell {

    int capacity ;
    mbr mbr ;
    Hashtable<String, hashValue> hashC ;

    myCell(double minX, double maxX, double minY, double maxY) {
        this.capacity = 4 ;
        this.mbr = new mbr(minX, maxX, minY, maxY) ;
        this.hashC = new Hashtable<>() ;
    }

}
