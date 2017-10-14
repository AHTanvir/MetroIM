package anwar.metroim.Utill;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by anwar on 10/4/2017.
 */

public class DocReader {
    public void readDocx(){
       // new DocumentViewerActivity().t();
  /*      File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard.getAbsolutePath().toString()+"/MetroIM/text2.docx".toString());
        StringBuilder content = new StringBuilder();
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("docRead "+line);
                content.append(line);
                content.append('\n');
            }
        }
        catch (IOException e) {
            System.out.println("docRead exception"+e);
        }*/
    }
}
