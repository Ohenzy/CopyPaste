import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearchFile {

    private String namePath;
    private ArrayList<String> nameFile;
    private long sizeFiles;
    private int quantityFiles;
    private boolean existFile;
    private volatile File path;

    public List<File> list;

    SearchFile() {
        list = new ArrayList<>();
    }


    public boolean isExistFile() {
        return existFile;
    }


    public void search(String nPath, ArrayList<String> nFile) {

        this.namePath = nPath;
        this.nameFile = nFile;

        path = new File(namePath);
        File[] fileList = path.listFiles();
        if (path.exists()) {
            existFile = true;


            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    search(fileList[i].toPath().toAbsolutePath().toString(), nameFile);

                }
                else {

                    for (int j = 0; j<nameFile.size();j++) {
                        if (fileList[i].getName().matches(".*" + nameFile.get(j) + ".*")) {
                            quantityFiles++;
                            try {
                                sizeFiles += Files.size(Paths.get(fileList[i].toPath().toAbsolutePath().toString()));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            list.add(fileList[i]);
                        }
                    }
                }
            }
        } else {

            existFile = false;
        }

    }

    public long getSizeFiles(){
        return sizeFiles;
    }
    public Integer getQuantityFiles(){
        return quantityFiles;
    }

}
