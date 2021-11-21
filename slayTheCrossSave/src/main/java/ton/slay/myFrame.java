package ton.slay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class myFrame extends JFrame {
    
    private JLabel outputConsole;
    private JTextField outputField;
    private JButton toPC;
    private JButton toPhone;
    private JButton exitButton;
    private JLabel sign;

    private JTextArea cli;
    private JScrollPane cliScr;

    private JPanel panel;

    private int FRAME_WIDTH = 300;
    private int FRAME_HEIGHT = 480;

    String newline = "\n";

    saveHandler sh;


    public myFrame(String str)  {
        super(str);
        
        sh = new saveHandler();

        createButtons();
        createConsoleField();
        createTextArea();
        createExitButton();

        createPanel();


        

        setSize(FRAME_WIDTH, FRAME_HEIGHT);

    }

    public myFrame()    {
        sh = new saveHandler();

        createButtons();
        createConsoleField();
        createTextArea();

        createPanel();


        

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    private void createTextArea()   {
        sign = new JLabel("mercury501");
        cli = new JTextArea("LOADED!\n", 20, 20);
        cliScr = new JScrollPane(cli);

    }

    private void createButtons() {
        toPC = new JButton("Move TO PC");
        toPhone = new JButton("Move TO Phone");

        class toPCListener implements ActionListener    {
            public void actionPerformed(ActionEvent event)  {
                sh.toPC(cli);
                //outputConsole.setText("Moved Save To PC!");
                
                //exitButton.setVisible(true);
            }
        }

        class toPhoneListener implements ActionListener {
            public void actionPerformed(ActionEvent event)  {
                sh.toPhone(cli);
                //outputConsole.setText("Moved Save To Phone!");
               
                
                //exitButton.setVisible(true);
            }
        }
        
        ActionListener tpPClsnr = new toPCListener();
        toPC.addActionListener(tpPClsnr);

        ActionListener toPhonelsnr = new toPhoneListener();
        toPhone.addActionListener(toPhonelsnr);

    }

    private void createExitButton() {
        exitButton = new JButton("EXIT");
        exitButton.setVisible(false);

        class exitListener implements ActionListener    {
            public void actionPerformed(ActionEvent event)  {
                System.exit(0);
            }
        }
        
        ActionListener exitLsnr = new exitListener();
        exitButton.addActionListener(exitLsnr); 
    }

    private void createPanel()  {
        this.panel = new JPanel();

        this.panel.add(toPC);
        this.panel.add(toPhone);
        //panel.add(outputConsole);
       
        
        this.panel.add(cli);
        this.panel.add(cliScr);
        this.panel.add(exitButton);
        this.panel.add(sign);
        

        //panel.add(outputField);

        add(this.panel);
        
    }

    private void createConsoleField()   {
        outputConsole = new JLabel("Choose Wisely...");
        final int FIELD_WIDTH = 10;

        //outputField = new JTextField(FIELD_WIDTH);

        //outputField.setText("ciao");
    }
}
