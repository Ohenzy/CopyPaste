import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;


public class PanelProgress extends JPanel {

    private final int quantityThread = 5;
    private  AtomicLong counterCopying;
    private double progressInPercent;
    private ConcurrentHashMap<File,Long> fileMap;
    private MainFrame frame;
    private ExecutorService fixThread;
    private String pathPaste;
    private JButton backButton;
    private JComponent canvas;
    private long timeSleep;
    private long indexLast;
    private String speedCopying;
    private ServerSocket serverSocket;
    private long timeLeft;


    PanelProgress(MainFrame f, String pathP) {
        setLayout(null);
        setBackground(Color.decode("#edeee7"));
        fileMap = new ConcurrentHashMap<>();
        frame = f;
        pathPaste = pathP;
        counterCopying = new AtomicLong(0);
        indexLast = 0;
        speedCopying = "";
        timeLeft = 0;
        try {
            serverSocket = new ServerSocket(2019);
        } catch (IOException e) {
            e.printStackTrace();
        }

/////////////// СОКЕТЫ /////////////////////////////////////////////
        new Thread(){
            @Override
            public void run(){
                try {
                    while (MainFrame.IN_PROCESS_COPYING){
                        serverSocket.accept();
                        new Thread() {
                            @Override
                            public void run() {
                                repaintProgress();
                            }
                        }.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
////////////////////////////////////////////////////////////////////

        new Thread(){
            @Override
            public void run() {
                fixThread = Executors.newFixedThreadPool(quantityThread);
                for (int index = 0; index < frame.getSearchFile().getQuantityFiles(); index++) {
                    fixThread.submit(new CopyFileThread(frame.getSearchFile().list.get(index), pathPaste,getPanelProgress()));
                }
            }
        }.start();

        backButton = new JButton("Отмена");
        backButton.setSize(140, 30);
        backButton.setLocation(200, MainFrame.SCREEN_SIZE / 3 + backButton.getHeight() * 5);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.IN_PROCESS_COPYING = false;
                try {
                    serverSocket.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
                frame.switchPanelCopy();
            }
        });

        canvas = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;


                DecimalFormat df = new DecimalFormat("0.0");
                g2.setPaint(Color.decode("#39a4ff"));
                g2.fill(new Rectangle(40, 230, (int) (progressInPercent * 3), 25));
                g2.setPaint(Color.BLACK);
                g2.draw(new Rectangle(40, 230, 299, 25));
                g2.setFont(new Font("Courier", 1, 18));
                g2.drawString(df.format(progressInPercent) + " %", MainFrame.SCREEN_SIZE / 2 - 25, 248);

                if(MainFrame.IN_PROCESS_COPYING)
                    drawProgressFile(g2);

                else
                    g2.drawString(" Копирование завершено", 70, 120);


            }
        };
        canvas.setSize(MainFrame.SCREEN_SIZE, MainFrame.SCREEN_SIZE - 20);

        add(backButton);
        add(canvas);


    }




    private void drawProgressFile(Graphics2D g2) {
        File[] file = new File[quantityThread];
        Iterator<File> iterator = fileMap.keySet().iterator();
        for (int i = 0; i < quantityThread; i++) {
            if (iterator.hasNext()) {
                file[i] = iterator.next();
            } else break;
        }

        int posY = 35;
        for (int i = 0; i < quantityThread; i++) {

            if (file[i] != null)
                if(fileMap.containsKey(file[i])) {

                    double progressInPercent = (fileMap.get(file[i]) / (double) file[i].length()) * 100;
                    DecimalFormat df = new DecimalFormat("0.0");

                    g2.setPaint(Color.decode("#39a4ff")); // Синий
                    g2.fill(new Rectangle(40, posY * (i + 1), (int) (progressInPercent * 1), 20));
                    g2.setPaint(Color.BLACK);
                    g2.draw(new Rectangle(40, posY * (i + 1), 99, 20));
                    g2.setFont(new Font("Courier", 1, 12));
                    g2.drawString(df.format(progressInPercent) + " %", 70, posY * (i + 1) + 15);

                    g2.setFont(new Font("Courier", 0, 13));
                    g2.drawString(file[i].getName(), 145, posY * (i + 1) + 15);
                    g2.drawString(speedCopying ,40,225);
                    g2.drawString("Осталось:  "+reTime(timeLeft),40,270);


                    if (progressInPercent == 100)
                        fileMap.remove(file[i]);


                }

        }
    }

    private PanelProgress getPanelProgress(){
        return this;
    }

    private String reTime(long time){

        Integer index = 0;
        while (time / 60 >= 1){
            index ++;
            time-=60;
        }


        if(index == 0)
            return time+"c";


        return index+"м "+time+"c ";
    }

    private String reType(double sizeFiles) {
        byte index = 0;
        while (sizeFiles >= 1000){
            sizeFiles/=1024d;
            index++;
        }
        DecimalFormat df = new DecimalFormat("0");
        String s = df.format(sizeFiles);

        String type = "";
        if(index == 0)
            type = " Б/c";

        else if(index == 1)
            type = " КБ/c";

        else if(index == 2)
            type = " МБ/c";

        else if(index == 3)
            type = " ГБ/c";

        return s + type;
    }

    public synchronized void updatePanel(File file, long fileProgress, int length)  {
        counterCopying.addAndGet(length);
        fileMap.put(file,fileProgress);
    }




    protected void repaintProgress(){
        progressInPercent = (counterCopying.get() / (double) frame.getSearchFile().getSizeFiles()) * 100;
        if (progressInPercent < 100) {
            if ((timeSleep + 1000) < System.currentTimeMillis()) {
                timeSleep = System.currentTimeMillis();
                speedCopying = reType(counterCopying.get() - indexLast);

                if((counterCopying.get() - indexLast) != 0)
                    timeLeft = (frame.getSearchFile().getSizeFiles() - counterCopying.get())/(counterCopying.get() - indexLast);

                indexLast = counterCopying.get();
                repaint();
            }
        }
        else {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            speedCopying = "Копирование завершено";
            MainFrame.IN_PROCESS_COPYING = false;
            backButton.setText("Назад");
            repaint();

        }


    }


}