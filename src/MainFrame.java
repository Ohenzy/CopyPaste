import javax.swing.JFrame;
import java.text.SimpleDateFormat;
import java.util.Date;


public  class MainFrame extends JFrame {

    public static final int SCREEN_SIZE = 400;
    public static String ERROR_MESSAGE;
    public static boolean IN_PROCESS_COPYING = false;

    private SearchFile searchFile;



    MainFrame(){

        searchFile = new SearchFile();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_SIZE,SCREEN_SIZE-20);
        setLocationRelativeTo(null);
        setTitle("Copy Paste");
        setResizable(false);
        setVisible(true);
        add(new PanelSearch(this));
        validate();

    }

    public void switchPanelCopy(){
        getContentPane().removeAll();
        getContentPane().add(new PanelSetting(this));
        validate();
    }

    public void switchPanelSearch(){

        searchFile = new SearchFile();
        getContentPane().removeAll();
        getContentPane().add(new PanelSearch(this));
        validate();
    }

    public void startCopying(String pathPaste){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd - HH_mm_ss");
        getContentPane().removeAll();
        getContentPane().add(new PanelProgress(this,pathPaste+"\\"+format.format(new Date())));
        validate();

    }



    public SearchFile getSearchFile(){
        return this.searchFile;
    }

}
