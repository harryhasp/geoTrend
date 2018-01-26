package mySystem;

import java.util.Hashtable;

public class myCell {

    int maxCapacity ;
    int curCapacity ;
    mbr mbr ;
    Hashtable<String, hashValue> hashC ;
    int level ;
    myCell leftUp, leftDown, rightUp, rightDown ;

    myCell(double minX, double maxX, double minY, double maxY, int level) {
        System.out.println("myCell init - level = " + level) ;
        this.maxCapacity = 4 ;
        this.curCapacity = 0 ;
        this.mbr = new mbr(minX, maxX, minY, maxY) ;
        this.hashC = new Hashtable<>() ;
        this.level = level ;
        this.leftUp = this.leftDown = this.rightUp = this.rightDown = null ;
    }

    int addKeyword (String keyword, myPoint point, long timestamp) {
        this.curCapacity++ ;
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
            System.out.println("--> We need to go down - level = " + this.level) ;
            double splitX = mbr.leftUp.longitude + ((mbr.rightDown.longitude - mbr.leftUp.longitude) / 2) ;
            double splitY = mbr.rightDown.latitude + ((mbr.leftUp.latitude - mbr.rightDown.latitude) / 2) ;
            int cellCase = -1 ;

            //System.out.println(point.longitude + " - " + point.latitude);
            //System.out.println(splitX + " - " + splitY);

            if ( (point.longitude < splitX) && (point.latitude >= splitY) ) {
                cellCase = 0 ;
                System.out.println("--> cellCase = 0") ;
                if (this.leftUp == null) {
                    System.out.println("--> We need to create leftUp") ;
                    this.leftUp = new myCell(mbr.leftUp.longitude, splitX, splitY, mbr.leftUp.latitude, this.level+1) ;
                    (this.leftUp).addKeyword(keyword, point, timestamp) ;
                }
                else {
                    System.out.println("--> We HAVE the leftUp") ;
                    (this.leftUp).addKeyword(keyword, point, timestamp) ;
                }
            }
            else if ( (point.longitude >= splitX) && (point.latitude >= splitY) ) {
                cellCase = 1 ;
                System.out.println("--> cellCase = 1") ;
                if (this.rightUp == null) {
                    System.out.println("--> We need to create rightUp") ;
                    this.rightUp = new myCell(splitX, mbr.rightDown.longitude, splitY, mbr.leftUp.latitude, this.level+1) ;
                    (this.rightUp).addKeyword(keyword, point, timestamp) ;
                }
                else {
                    System.out.println("--> We HAVE the rightUp") ;
                    (this.rightUp).addKeyword(keyword, point, timestamp) ;
                }
            }
            else if ( (point.longitude < splitX) && (point.latitude < splitY) ) {
                cellCase = 2 ;
                System.out.println("--> cellCase = 2") ;
                if (this.leftDown == null) {
                    System.out.println("--> We need to create leftDown") ;
                    this.leftDown = new myCell(mbr.leftUp.longitude, splitX, mbr.rightDown.latitude, splitY, this.level+1) ;
                    (this.leftDown).addKeyword(keyword, point, timestamp) ;
                }
                else {
                    System.out.println("--> We HAVE the leftDown") ;
                    (this.leftDown).addKeyword(keyword, point, timestamp) ;
                }
            }
            else if ( (point.longitude >= splitX) && (point.latitude < splitY) ) {
                cellCase = 3 ;
                System.out.println("--> cellCase = 3") ;
                if (this.rightDown == null) {
                    System.out.println("--> We need to create rightDown") ;
                    this.rightDown = new myCell(splitX, mbr.rightDown.longitude, mbr.rightDown.latitude, splitY, this.level+1) ;
                    (this.rightDown).addKeyword(keyword, point, timestamp) ;
                }
                else {
                    System.out.println("--> We HAVE the rightDown") ;
                    (this.rightDown).addKeyword(keyword, point, timestamp) ;
                }
            }
            else {
                System.out.println("----> SOMETHING STRANGE IS GOING ON") ;
            }


            return 1 ;
        }
        else {
            //this.curCapacity++ ;
            if (this.curCapacity > this.maxCapacity) {
                System.out.println("--> We JUST exceeded the capacity") ;
                return 2 ;
            }
            else { // we still have space
                return 0 ;
            }
        }
        /*
        if (this.curCapacity > this.maxCapacity) {  //
            System.out.println("--> We had already exceeded the capacity") ;
            this.curCapacity++ ;
            return 1 ;
        }
        else {
            //this.curCapacity++ ;
            if (this.curCapacity > this.maxCapacity) {
                System.out.println("--> We JUST exceeded the capacity") ;
                return 2 ;
            }
            else { // we still have space
                return 0 ;
            }
        }
        */
    }

}
