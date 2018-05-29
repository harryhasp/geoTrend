package approximateRreplicateUncertain;

public class myPolygon {

    mbr mbrPolygon ;
    int type ; // 0 point, 1 polygon
    double portion ;

    myPolygon (mbr mbrPolygon, int type) {
        this.mbrPolygon = mbrPolygon ;
        this.type = type ;
        this.portion = 1.0 ;
    }

}
