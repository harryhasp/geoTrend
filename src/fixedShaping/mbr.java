package fixedShaping;


public class mbr {

    myPoint leftUp ;
    myPoint rightDown ;

    mbr(double minX, double maxX, double minY, double maxY) {
        this.leftUp = new myPoint(minX, maxY) ;
        this.rightDown = new myPoint(maxX, minY) ;
    }
}
