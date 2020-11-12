package com.brbr.brick.debug;

import com.brbr.brick.InputManager;
import com.brbr.brick.MouseEventListener;

import javax.swing.*;
import java.awt.*;

public class TestGUI {
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MouseEventListener l = InputManager.getInstance().mouseEventListener;
        JPanel primary = new JPanel(){
            Point src = new Point();
            Point dest = new Point();

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.red);
                com.brbr.brick.debug.Debugger.Print("src: ",l.getSource());
                com.brbr.brick.debug.Debugger.Print("dest: ",l.getDestination());
                g.drawLine(l.getSource().x, l.getSource().y, l.getDestination().x, l.getDestination().y);
            }
        };
        primary.addMouseListener(l);
        primary.addMouseMotionListener(l);

        frame.getContentPane().add(primary);
        frame.setSize(370,680);
        frame.setVisible(true);

    }
}