package mySystem;

import java.io.*;

public class indexShapingClass {

    public void indexShaping(String sampleDataFile) {
        try {
            File file = new File("test.txt") ;
            FileReader fileReader = null ;
            fileReader = new FileReader(file) ;
            BufferedReader bufferedReader = new BufferedReader(fileReader) ;
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
