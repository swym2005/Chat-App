import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server implements ActionListener {
    static JLabel l4;
    static JLabel offline;
    static JPanel p1 = new JPanel();
    static JFrame frame = new JFrame();
    static JPanel p2 = new JPanel();
    JTextField text = new JTextField();
    JButton button = new JButton("Send");
    static Box vertical = Box.createVerticalBox();
    static JScrollPane scrollPane;
    JLabel file_chooser = new JLabel(new ImageIcon("Icons/file_add.png"));
    static ObjectOutputStream objOut;
    static ObjectInputStream objIn;

    Server() {
        frame.setLayout(null);

        //Top green panel stuff
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);

        //label for back icon
        JLabel l1 = new JLabel(new ImageIcon("Icons/back_icon.png"));
        l1.setBounds(5, 21, 25, 25);
        l1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });


        //label for profile icon
        JLabel l2 = new JLabel(new ImageIcon(new ImageIcon("Icons/Profile_icon.png").getImage().getScaledInstance(45, 45, Image.SCALE_DEFAULT)));
        l2.setBounds(40, 12, 45, 45);


        //label for the name of the person.
        JLabel l3 = new JLabel("Viraj");
        l3.setBounds(93, 13, 100, 25);
        l3.setFont(new Font("Segoe UI", Font.BOLD, 19));
        l3.setForeground(Color.WHITE);


        //Status label
        l4 = new JLabel("Online");
        l4.setBounds(94, 35, 100, 18);
        l4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l4.setForeground(Color.WHITE);



        //Added everything to the top green panel
        p1.add(l1); p1.add(l2); p1.add(l3); p1.add(l4);
        frame.add(p1);

        //panel for the chat Area
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

        //Scrollpane for the chat area.
        scrollPane = new JScrollPane(p2);
        scrollPane.setBounds(5, 75, 440, 570);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane);


        // textfield for sending the texts
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        frame.add(text);


        //Send button
        button.setBounds(370, 655, 75, 40);
        button.setBackground(new Color(7, 94, 84));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.addActionListener(this);
        frame.add(button);


        //File chooser to send the files
        file_chooser.setBounds(315, 649, 50, 50);
        file_chooser.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int resp = fileChooser.showOpenDialog(null);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        sendFile(file);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        frame.add(file_chooser);


        // Setting the main frame
        frame.setSize(450, 700);
        frame.setLocation(200, 50);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String out = text.getText();
        if (!out.isEmpty()) {
            try {
                objOut.writeObject("TEXT:" + out);
                addMessage(out, true);
                text.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    void sendFile(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        objOut.writeObject("FILE:" + file.getName() + ":" + data.length);
        objOut.writeObject(data);
        objOut.flush();
        addMessage("Sent file: " + file.getName(), true);
    }

    static void addMessage(String msg, boolean isSender) {
        JPanel panel = isSender ? formatLabel(msg) : formatOppLabel(msg);
        JPanel align = new JPanel(new BorderLayout());
        align.add(panel, isSender ? BorderLayout.LINE_END : BorderLayout.LINE_START);
        vertical.add(align);
        vertical.add(Box.createVerticalStrut(15));
        p2.add(vertical);
        frame.validate();
        scrollToBottom();
    }

    public static void scrollToBottom() {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setValue(verticalBar.getMaximum());
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        panel.add(output);
        JLabel time = new JLabel(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
        panel.add(time);
        return panel;
    }

    public static JPanel formatOppLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        output.setBackground(new Color(173, 216, 230));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        panel.add(output);
        JLabel time = new JLabel(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
        panel.add(time);
        return panel;
    }

    public static void main(String[] args) {
        new Server();
        try {
            ServerSocket skt = new ServerSocket(6001);
            Socket s = skt.accept();
            objOut = new ObjectOutputStream(s.getOutputStream());
            objIn = new ObjectInputStream(s.getInputStream());

            while (true) {
                Object obj = objIn.readObject();
                if (obj instanceof String msg) {
                    if (msg.startsWith("FILE:")) {
                        String[] parts = msg.split(":", 3);
                        String filename = parts[1];
                        byte[] fileData = (byte[]) objIn.readObject();
                        FileOutputStream fos = new FileOutputStream("received_" + filename);
                        fos.write(fileData);
                        fos.close();
                        addMessage("Received file: " + filename, false);
                    } else if (msg.startsWith("TEXT:")) {
                        addMessage(msg.substring(5), false);
                    }
                }
            }
        } catch (Exception ex) {
            l4.setText("Offline");
            p1.revalidate();
            p1.repaint();
            ex.printStackTrace();
        }
    }
}
