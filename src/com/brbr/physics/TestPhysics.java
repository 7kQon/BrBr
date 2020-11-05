package com.brbr.physics;

import com.brbr.brick.object.GameObject;
import com.brbr.math.Bounds;
import com.brbr.math.Transform;
import com.brbr.math.Vector2;

import javax.swing.*;
import java.awt.*;

public class TestPhysics extends JFrame {
    public class Canvas extends JPanel {
        Ball[] balls;
        public Canvas(){}
        public Canvas(Ball[] balls){this.balls = balls;}

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            for(var ball: balls){
                Bounds bounds = ((BoxCollider)ball.getComponent("BoxCollider")).bounds;
                g.setColor(Color.RED);
                g.drawOval((int)bounds.getMinX(), (int)bounds.getMinY(), ball.size, ball.size);
            }

            for(var wall: walls){
                int x, y, width, height;
                BoxCollider collider = (BoxCollider)(wall.getComponent("BoxCollider"));
                x = (int)(collider.bounds.getMinX());
                y = (int)(collider.bounds.getMinY());
                width = collider.bounds.getWidth();
                height = collider.bounds.getHeight();
                g.setColor(Color.BLACK);
                g.fillRect(x,y,width,height);
            }
        }
    }

    private Canvas canvas;
    private PhysicManager physicManager;
    private Ball ball;
    private Ball[] balls;
    private GameObject[] walls;
    private final int width = 400;
    private final int height = 700;
    private double lastTime = 0;

    public TestPhysics(){
        physicManager  = PhysicManager.getInstance();

        balls = new Ball[3];
        balls[0] = new Ball(100,350);
        balls[1] = new Ball(200,350);
        balls[2] = new Ball(300, 350);


        walls = new GameObject[4];
        walls[0] = new GameObject(width/2,0);
        ((BoxCollider) walls[0].addComponent(new BoxCollider(width, 10, ColliderType.STATIC))).setTag("wall0");
        walls[1] = new GameObject(0,height/2);
        ((BoxCollider) walls[1].addComponent(new BoxCollider(10, height, ColliderType.STATIC))).setTag("wall1");
        walls[2] = new GameObject(width/2, height);
        ((BoxCollider) walls[2].addComponent(new BoxCollider(width, 10, ColliderType.STATIC))).setTag("wall2");
        walls[3] = new GameObject(width,height/2);
        ((BoxCollider) walls[3].addComponent(new BoxCollider(10, height, ColliderType.STATIC))).setTag("wall3");

        for(int i = 0; i < 4; i++)
            physicManager.addEntity((BoxCollider)(walls[i].getComponent("BoxCollider")));

        for(int i = 0; i < 3; i++){
            physicManager.addEntity((BoxCollider)(balls[i].getComponent("BoxCollider")));
        }
        for(var ball: balls){
            ball.throwBall(-45);
        }

        canvas = new Canvas(balls);
        this.add(canvas);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(width, height));
        setVisible(true);

        lastTime = System.currentTimeMillis();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double dt = (System.currentTimeMillis() - lastTime) / 1000.0;
                lastTime = System.currentTimeMillis();
                physicManager.collisionCheck();
                for(var ball: balls){
                    ball.update(dt);
                }

                this.repaint();
            }
        }).start();
    }


    public static void main(String[] args){
        new TestPhysics();
    }
}
