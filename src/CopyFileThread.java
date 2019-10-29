import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public class CopyFileThread extends Thread {

    private final int port = 126984;
    private static int SIZE_BUFFER;
    private String pathResource;
    private String pathPaste;
    private File file;
    private AtomicLong progressFile;
    private PanelProgress panel;
    private byte[] buffer;
    private BufferedOutputStream outStream;
    private BufferedInputStream inStream;


     CopyFileThread(File f, String nPaste,PanelProgress panel) {

         this.panel = panel;
         file = f;
         setName(file.getName());
         pathResource = file.toPath().getParent().toString();
         pathPaste = nPaste;
         progressFile = new AtomicLong(0);
    }

    @Override
    public void run() {

        char[] tmp = new char[pathResource.length()];
        for (int i = 0; i < pathResource.length(); i++) {
            if (pathResource.charAt(i) != '\\' && pathResource.charAt(i) != ':')
                tmp[i] = pathResource.charAt(i);

            else
                tmp[i] = '_';
        }
        pathPaste = pathPaste + "\\" + new String(tmp);


        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            outStream = new BufferedOutputStream(new FileOutputStream(Files.createDirectories(Paths.get(pathPaste)) +"\\"+file.getName(),true));
            buffer = new byte[SIZE_BUFFER];

            int length;
            while ((length = inStream.read(buffer)) > 0){
                if(MainFrame.IN_PROCESS_COPYING) {
                    outStream.write(buffer, 0, length);
                    outStream.flush();
                    panel.updatePanel(file, progressFile.addAndGet(length), length);
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(InetAddress.getLocalHost(),port));
                    socket.close();

                }
                else{
                    inStream.close();
                    outStream.close();
                    return;
                }
            }
            inStream.close();
            outStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSizeBuffer(int buffer){
        SIZE_BUFFER = buffer;
    }

}
