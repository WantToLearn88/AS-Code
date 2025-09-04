package arman.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtil {

    ///File file;

    /*public FileUtil(String filePath){
        file = new File(filePath);
    }
    */

    public void read(){

    }

    public void write(){

    }

    public void delete(){

    }

    public void rename(){

    }

    public void copy(){

    }

    public void move(){

    }

    public boolean saveFile(File file, byte[] content){
        if (file == null) return false;
        try (OutputStream output = new FileOutputStream(file)) {
            output.write(content);
            return true;
        } catch (IOException ignored) {}
        return false;
    }

}
