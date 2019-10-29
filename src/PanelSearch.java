import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class PanelSearch extends JPanel {
        private JButton clearButton;
        private JButton searchButton;
        protected JTextField textResource;
        private JTextField textFile;
        private JComponent canvas;
        private MainFrame frame;
        private JButton chooseButton;
        private JRadioButton oneButton;
        private JRadioButton twoButton;
        private JButton chooseList;


    PanelSearch(MainFrame f){
        frame = f;
        setLayout(null);
        setBackground(Color.decode("#edeee7"));
        clearButton = new JButton("Очистить");
        clearButton.setSize(140,30);
        clearButton.setLocation(200,MainFrame.SCREEN_SIZE/3+clearButton.getHeight()*5);

        searchButton = new JButton("Найти");
        searchButton.setSize(140,30);
        searchButton.setLocation(40,MainFrame.SCREEN_SIZE/3+ searchButton.getHeight()*5);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (oneButton.isSelected()) {
                    MainFrame.ERROR_MESSAGE = textResource.getText() + "  :(";
                    ArrayList<String> listName = new ArrayList<>();
                    listName.add(textFile.getText());

                    frame.getSearchFile().search(textResource.getText(), listName);
                    frame.switchPanelCopy();


                } else {
                    ArrayList<String> listName = new ArrayList<>();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(textFile.getText()));
                        while (reader.ready()) {
                            listName.add(reader.readLine());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    frame.getSearchFile().search(textResource.getText(), listName);
                    frame.switchPanelCopy();
                }

            }

        });

        textResource = new JTextField();
        textResource.setLocation(40,70);
        textResource.setSize(300,25);


        textFile = new JTextField();
        textFile.setLocation(60,200);
        textFile.setSize(190,25);

        chooseList = new JButton("Открыть");
        chooseList.setVisible(false);
        chooseList.setLocation(255,200);
        chooseList.setSize(85,23);
        chooseList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showDialog(null,"Выбрать файл");
                textFile.setText(fileChooser.getSelectedFile().getPath());

            }
        });

        oneButton = new JRadioButton("Поиск по названию");
        oneButton.setSelected(true);
        oneButton.setSize(400,25);
        oneButton.setLocation(40,150);
        oneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseList.setVisible(false);

            }
        });

        twoButton = new JRadioButton("Выбрать файл со списком");
        twoButton.setSelected(false);
        twoButton.setSize(400,25);
        twoButton.setLocation(40,170);
        twoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseList.setVisible(true);

            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(oneButton);
        group.add(twoButton);


        chooseButton = new JButton("Открыть");
        chooseButton.setLocation(255,105);
        chooseButton.setSize(85,23);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileOpen.showDialog(null, "Выбрать папку");
                textResource.setText(fileOpen.getSelectedFile().toPath().toString());
            }
        });




        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFile.setText("");
                textResource.setText("");

            }
        });

        canvas = new JComponent(){
            @Override
            public void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g;

                g2.setFont(new Font("Courier",1,13));
                g2.drawString("Где искать:",40,65);
//                g2.drawString("Что искать:",40,230);

            }
        };

        canvas.setSize(MainFrame.SCREEN_SIZE,MainFrame.SCREEN_SIZE-20);


        add(oneButton);
        add(twoButton);
        add(textFile);
        add(textResource);
        add(searchButton);
        add(clearButton);
        add(canvas);
        add(chooseButton);
        add(chooseList);

        }
    }


