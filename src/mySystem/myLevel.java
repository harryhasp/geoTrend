package mySystem;

public class myLevel {

    myCell[] myCells ;

    myLevel(int depth) {
        System.out.println("myLevel init") ;
        //this.myCells = new myCell[(int) (Math.pow(4, depth))] ;
        this.myCells = new myCell[2] ;
        System.out.println("myLevel init 2") ;
    }

    void addKeyword (String keyword) {

    }

}
