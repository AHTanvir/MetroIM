package anwar.metroim.Utill;

import android.os.Environment;



import java.io.File;


/**
 * Created by anwar on 10/4/2017.
 */

public class DocumentViewerActivity {
    public void t() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard.getAbsolutePath().toString() + "/MetroIM/text2.docx".toString());
    }
}
