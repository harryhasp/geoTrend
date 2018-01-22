package mySystem;

public class myLevel {

    myCell[] myCells ;

    myLevel(int depth) {
        this.myCells = new myCell[(int) (Math.pow(4, depth))] ;
    }

}
