package cn.myedit;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class main {
    public static void main(String[] args) {
        FileWindow win = new FileWindow();
        win.pack();
        win.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        win.setBounds(200, 180, 550, 360);
        win.setVisible(true);

    }
}
