package cn.myedit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileWindow extends JFrame implements ActionListener, Runnable {
    Thread compiler = null;
    Thread run_prom = null;
    boolean bn = true;
    CardLayout myCard; // 声明卡片式布局，以后会用到
    File file_saved = null;
    // 按钮的定义
    JButton button_input_txt, button_compiler, button_compiler_text, button_run_prom, button_see_doswin;
    JPanel p = new JPanel();
    // 程序输入区
    JTextArea input_text = new JTextArea();
    // 编译错误显示区
    JTextArea compiler_text = new JTextArea();
    // 程序的输出信息
    JTextArea dos_out_text = new JTextArea();

    JTextField input_file_name_text = new JTextField();
    JTextField run_file_name_text = new JTextField();

    public FileWindow() {
        super("Java语言编辑器");
        myCard = new CardLayout();
        compiler = new Thread(this);
        run_prom = new Thread(this);
        button_input_txt = new JButton("程序输入区（白色）");
        button_compiler_text = new JButton("编译结果区（粉红色）");
        button_see_doswin = new JButton("程序运行结果（浅蓝色）");
        button_compiler = new JButton("编译程序");
        button_run_prom = new JButton("运行程序");

        // 设置卡片布局
        p.setLayout(myCard);
        // 定义卡片名称
        p.add("input", input_text);
        p.add("compiler", compiler_text);
        p.add("dos", dos_out_text);
        add(p, "Center");

        compiler_text.setBackground(Color.pink);
        dos_out_text.setBackground(Color.cyan);
        JPanel pl = new JPanel();

        pl.setLayout(new GridLayout(3, 3));
        // 添加组件
        pl.add(button_input_txt);
        pl.add(button_compiler_text);
        pl.add(button_see_doswin);
        pl.add(new JLabel("输入编译文件名（.java）： "));
        pl.add(input_file_name_text);
        pl.add(button_compiler);
        pl.add(new JLabel("输入应用程序主类名: "));
        pl.add(run_file_name_text);
        pl.add(button_run_prom);
        add(pl, "North");

        // 定义事件
        button_input_txt.addActionListener(this);
        button_compiler.addActionListener(this);
        button_compiler_text.addActionListener(this);
        button_run_prom.addActionListener(this);
        button_see_doswin.addActionListener(this);

    }

    public void actionPerformed(ActionEvent e) {
        // 实现方法
        if (e.getSource() == button_input_txt) {
            // 显示程序输入区
            myCard.show(p, "input");
        } else if (e.getSource() == button_compiler_text) {
            // 显示编译结果显示区
            myCard.show(p, "compiler");
        } else if (e.getSource() == button_see_doswin) {
            // 显示程序运行结果区
            myCard.show(p, "dos");
        } else if (e.getSource() == button_compiler) {
            // 如果是编译按钮，执行编译文件的方法
            if (!(compiler.isAlive())) {
                compiler = new Thread(this);
            }
            try {
                compiler.start();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            myCard.show(p, "compiler");
        } else if (e.getSource() == button_run_prom) {
            // 如果是运行按钮，执行运行文件的方法
            if (!(run_prom.isAlive())) {
                run_prom = new Thread(this);
            }
            try {
                run_prom.start();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            myCard.show(p, "dos");
        }


    }

    @Override
    public void run() {
        // 实现方法
        if (Thread.currentThread() == compiler) {
            compiler_text.setText(null);
            String temp = input_text.getText().trim();
            byte[] buffer = temp.getBytes();
            int b = buffer.length;
            String file_name = null;
            file_name = input_file_name_text.getText().trim();
            try {
                file_saved = new File(file_name);
                FileOutputStream writefile = null;
                writefile = new FileOutputStream(file_saved);
                writefile.write(buffer, 0, b);
                writefile.close();
            } catch (Exception e) {
                System.out.println("ERROR");
            }
            try {
                // 获取该进程的错误流,才可以知道运行结果到底是失败了还是成功了
                Runtime rt = Runtime.getRuntime();
                // 通过Runtime调用javac命令。注意："javac "这个字符串是有一个空格的
                InputStream in = rt.exec("javac " + file_name).getErrorStream();
                BufferedInputStream bufIn = new BufferedInputStream(in);
                byte[] shuzu = new byte[100];
                int n = 0;
                boolean flag = true;
                while ((n = bufIn.read(shuzu, 0, shuzu.length)) != -1) {
                    String s = null;
                    s = new String(shuzu, 0, n);
                    compiler_text.append(s);
                    if (s != null) {
                        flag = false;
                    }
                }
                // 判断是否编译成功
                if (flag) {
                    compiler_text.append("Compile Succeed!");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (Thread.currentThread() == run_prom) {
            dos_out_text.setText(null);
            try {
                Runtime rt = Runtime.getRuntime();
                String path = run_file_name_text.getText().trim();
                // 调用Java命令
                Process stream = rt.exec("javac " + path);
                InputStream in = stream.getInputStream();
                BufferedInputStream bisErr = new BufferedInputStream(stream.getErrorStream());
                BufferedInputStream bisIn = new BufferedInputStream(in);
                byte[] buf = new byte[150];
                byte[] err_buf = new byte[150];

                @SuppressWarnings("unused")
                int m = 0;
                @SuppressWarnings("unused")
                int i = 0;

                String s = null;
                String err = null;

                // 打印编译信息及错误消息
                while ((m = bisIn.read(buf, 0, 150)) != -1) {
                    s = new String(buf, 0, 150);
                    dos_out_text.append(s);

                }
                while ((i = bisErr.read(err_buf)) != -1) {
                    err = new String(err_buf, 0, 150);
                    dos_out_text.append(err);
                }


            } catch (Exception e) {

            }
        }


    }


}
