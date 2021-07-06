import org.recognition.database.DB;
import org.recognition.io.WavReader;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Insert {
    public static void main(String[] arg){
        DB db = new DB();
        File file = new File(arg[0]);
        String[] name;
        if(file.isDirectory())
            name = file.list();
        else {
            name = new String[1];
            name[0] = "\b";
        }
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        if (name != null) {
            for (String aName : name) {
                if(!aName.endsWith(".wav"))
                    continue;
                String filename = arg[0] + File.separator + aName;
                executorService.execute(() -> {
                    System.out.println(filename);
                    db.insert(new WavReader(filename));
                });
            }
        }

        executorService.shutdown();
    }

}
