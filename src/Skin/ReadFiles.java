package Skin;

import java.io.File;

public class ReadFiles {

    public File[] getFiles(String folderpath){
        File folder = new File(folderpath);
        File[] files = folder.listFiles();
        return files;

    }
}
