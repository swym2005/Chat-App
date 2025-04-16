
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Client implements ActionListener {
    static JLabel l4;
    static JLabel offline;
    static JPanel p1;
    JTextField text;
    JButton button;
    static JPanel p2;
    static Box vertical = Box.createVerticalBox();
    static JFrame frame = new JFrame();
    static ObjectOutputStream objOut;
    static ObjectInputStream objIn;
    static JScrollPane scrollPane;

    Client() {
        frame.setLayout(null);
        p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        frame.add(p1);

        JLabel l1 = new JLabel(new ImageIcon("Icons/back_icon.png"));
        l1.setBounds(5, 21, 25, 25);
        l1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        p1.add(l1);

        JLabel l2 = new JLabel(new ImageIcon(new ImageIcon("Icons/Profile_icon.png").getImage().getScaledInstance(45, 45, Image.SCALE_DEFAULT)));
        l2.setBounds(40, 12, 45, 45);
        p1.add(l2);

        JLabel l3 = new JLabel("Swayam");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 19));
        l3.setForeground(Color.WHITE);
        l3.setBounds(93, 13, 100, 25);
        p1.add(l3);

        l4 = new JLabel("Online");
        l4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l4.setForeground(Color.WHITE);
        l4.setBounds(94, 35, 100, 18);


        p1.add(l4);

        offline = new JLabel("Offline");
        offline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        offline.setForeground(Color.WHITE);
        offline.setBounds(94, 35, 100, 18);


        p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(p2);
        scrollPane.setBounds(5, 75, 440, 570);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane);

        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        frame.add(text);

        button = new JButton("Send");
        button.setBounds(370, 655, 75, 40);
        button.setBackground(new Color(7, 94, 84));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.addActionListener(this);
        frame.add(button);

        JLabel file_chooser = new JLabel(new ImageIcon("Icons/file_add.png"));
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

        frame.setSize(450, 700);
        frame.setLocation(700, 50);
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

    public static JPanel formatLabel(String msg) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + msg + "</p></html>");
        output.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        panel.add(output);
        JLabel time = new JLabel(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
        panel.add(time);
        return panel;
    }

    public static JPanel formatOppLabel(String msg) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + msg + "</p></html>");
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
        new Client();
        try {
            Socket s = new Socket("192.168.206.207", 6001);
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
