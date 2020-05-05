import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class CopyFileHDFS {
    public static void main(String[] args) {

       /* Path pt = new Path("./prova.txt");
        try {
            Configuration configuration = new Configuration();
            configuration.set("fs.default.name","hdfs://master:54310");
            FileSystem fs = pt.getFileSystem(configuration);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));
            String line = null;

            line = br.readLine();

            while (line != null) {

                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(line);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
       System.out.println("Sono svegliooo");

    }
}
