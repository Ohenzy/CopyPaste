import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanelSetting extends JPanel {

    private MainFrame frame;
    private JTextField textPaste;
    private JTextField textBuffer;
    private JButton backButton;
    private JButton copyButton;
    private JComponent canvas;
    private JButton chooseButton;
    private JRadioButton standard;
    private JRadioButton customization;
    private ButtonGroup group;


    PanelSetting(MainFrame f ) {
        frame = f;
        setLayout(null);
        setBackground(Color.decode("#edeee7"));

        textPaste = new JTextField();
        textPaste.setLocation(40, 95);
        textPaste.setSize(300,25);


        standard = new JRadioButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(standard.isSelected())
                    textBuffer.setEnabled(false);
            }
        });
        standard.setText("Стандартный размер буфера");
        standard.setSize(250,30);
        standard.setLocation(40,160);
        standard.setSelected(true);

        customization = new JRadioButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(customization.isSelected())
                    textBuffer.setEnabled(true);
            }
        });

        customization.setText("Настроить (не рекомендуется)");
        customization.setSize(250,30);
        customization.setLocation(40,185);

        group = new ButtonGroup();
        group.add(customization);
        group.add(standard);

        textBuffer = new JTextField("0");
        textBuffer.setLocation(45,215);
        textBuffer.setSize(60,25);
        textBuffer.setEnabled(false);


        backButton = new JButton("Назад");
        backButton.setSize(140, 30);
        backButton.setLocation(200, MainFrame.SCREEN_SIZE / 3 + backButton.getHeight() * 5);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                frame.switchPanelSearch();
            }
        });



        copyButton = new JButton("Копировать");
        copyButton.setSize(140, 30);
        copyButton.setLocation(40, MainFrame.SCREEN_SIZE / 3 + backButton.getHeight() * 5);
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (textPaste.getText().equals(""))
                    textPaste.setText("\\CopyPaste\\");



                if (customization.isSelected()) {
                    Integer i = new Integer(textBuffer.getText());
                    if (i > 0)
                        CopyFileThread.setSizeBuffer(i);
                    else
                        CopyFileThread.setSizeBuffer(1048576);
                }
                else
                    CopyFileThread.setSizeBuffer(1048576);


                    MainFrame.IN_PROCESS_COPYING = true;
                    frame.startCopying(textPaste.getText());

            }
        });

        chooseButton = new JButton("Открыть");
        chooseButton.setLocation(254,130);
        chooseButton.setSize(85,23);
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileOpen.showDialog(null, "Выбрать папку");
                textPaste.setText(fileOpen.getSelectedFile().toPath().toString());
            }
        });


        canvas = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;


                g2.setFont(new Font("Courier", 1, 14));

                if (frame.getSearchFile().isExistFile() && frame.getSearchFile().getQuantityFiles() != 0 ) {
                    copyButton.setEnabled(true);
                    g2.drawString("Файлов найдено:    " + frame.getSearchFile().getQuantityFiles().toString(), 40, 30);
                    g2.drawString("Общий размер:       " + reType((double) frame.getSearchFile().getSizeFiles()), 40, 50);
                    g2.drawString("Скопировать в:", 40, 90);
                    g2.setFont(new Font("Courier", 0, 12));
                    g2.drawString("Байт",107,234);
                } else {
                    chooseButton.setVisible(false);
                    copyButton.setVisible(false);
                    textPaste.setVisible(false);
                    standard.setVisible(false);
                    customization.setVisible(false);
                    textBuffer.setVisible(false);

                    g2.drawString("Совпадений не найдено:", 40, 30);
                    g2.drawString(MainFrame.ERROR_MESSAGE, 40, 50);
                }
            }
        };
        canvas.setSize(MainFrame.SCREEN_SIZE, MainFrame.SCREEN_SIZE-20);



        add(textBuffer);
        add(standard);
        add(customization);
        add(copyButton);
        add(textPaste);
        add(backButton);
        add(canvas);
        add(chooseButton);
    }



    private String reType(Double sizeFiles) {
        byte index = 0;
        while (sizeFiles >= 1000){
            sizeFiles/=1024;
            index++;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String s = df.format(sizeFiles);

        String type = "";
        if(index == 0)
            type = " Байт";

        else if(index == 1)
            type = " КБ";

        else if(index == 2)
            type = " МБ";

        else if(index == 3)
            type = " ГБ";

        return s + type;
    }
}
