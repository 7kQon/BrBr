package com.brbr.brick.level;

import com.brbr.brick.Scene;
import com.brbr.brick.assets.Colors;
import com.brbr.brick.assets.Coordinates;
import com.brbr.brick.math.Transform;
import com.brbr.brick.math.Vector2;
import com.brbr.brick.object.BallItem;
import com.brbr.brick.object.Brick;
import com.brbr.brick.object.GameObject;
import com.brbr.brick.object.Particle;
import com.brbr.brick.physics.Ball;
import com.brbr.brick.physics.BoxCollider;
import com.brbr.brick.physics.CircleCollider;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LevelManager {

    private Scene scene;

    public LevelManager(Scene scene) {
        this.scene = scene;
    }

    public void createBrickParticles(List particles, Brick brick) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                BoxCollider collider = (BoxCollider) (brick.getComponent("BoxCollider"));

                Vector2 spawnPos = new Vector2(
                        collider.bounds.getMinX() + j * Coordinates.PARTICLE_SIZE,
                        collider.bounds.getMinY() + i * Coordinates.PARTICLE_SIZE
                );
                Particle particle = new Particle(spawnPos, Colors.BRICK_COLOR_LEVEL[0]);
                particles.add(particle);
            }
        }
    }

    public void createItemParticles(List particles, BallItem item) {
        for (int i = 0; i < 20; i++) {
            CircleCollider collider = (CircleCollider) item.getComponent("CircleCollider");
            Vector2 spawnPos = new Vector2(
                    collider.center.x + Math.random() * Coordinates.BALL_SIZE - Coordinates.BALL_SIZE / 2,
                    collider.center.y + Math.random() * Coordinates.BALL_SIZE - Coordinates.BALL_SIZE / 2
            );
            Particle particle = new Particle(spawnPos, Color.GREEN);
            particles.add(particle);
        }
    }

    public void update(long dt) {
        if (scene.gameStatus != Scene.PROCEEDING_GAME) return;

        if (scene.needLevelUpdate) {
            createNewLevel();
            scene.needLevelUpdate = false;
        } else {
            List<GameObject> particleList = new ArrayList();

            scene.gameObjectList = scene.gameObjectList.stream()
                    .filter(gameObject -> {
                        if (gameObject instanceof Brick) {
                            Brick brick = ((Brick) gameObject);

                            if (brick.health > 0) return true;

                            createBrickParticles(particleList, brick);
                            return false;
                        }
                        if (gameObject instanceof BallItem) {
                            BallItem item = ((BallItem) gameObject);
                            if (!item.isEaten) return true;

                            scene.ballCount++;
                            createItemParticles(particleList, item);
                            return false;
                        } else return true;
                    })
                    .collect(Collectors.toList());
            scene.gameObjectList.addAll(particleList);

            long ballCount = scene.gameObjectList.stream()
                    .filter(gameObject -> gameObject instanceof Ball)
                    .count();
            if (ballCount == 0 && !scene.needToShoot) {
                scene.needLevelUpdate = true;
                scene.needToShoot = true;
            }

        }

        if (scene.gameStatus == Scene.END_GAME) {
            scene.level = 0;
            scene.ballCount = 1;
            scene.gameObjectList = scene.gameObjectList.stream()
                    .filter(gameObject -> !(gameObject instanceof Brick))
                    .collect(Collectors.toList());
        }
    }

    private void createNewLevel() {
        scene.level++;
        scene.scoreManager.updateScore(1);

        int maxLevel = 0;
        for (GameObject gameObject : scene.gameObjectList) {
            if (gameObject instanceof Brick) {
                Brick brick = (Brick) gameObject;
                Vector2 newPosition = new Vector2(brick.transform.position.x, brick.transform.position.y);
                newPosition.y += Coordinates.BRICK_HEIGHT + Coordinates.BRICK_MARGIN;
                brick.setPosition(newPosition);
                brick.animateMove();
                BoxCollider collider = ((BoxCollider) brick.getComponent("BoxCollider"));
                int brickLevel = (int) (collider.bounds.getCenter().y / (Coordinates.BRICK_HEIGHT + Coordinates.BRICK_MARGIN));
                if (maxLevel < brickLevel) maxLevel = brickLevel;
            }

            if (gameObject instanceof BallItem) {
                BallItem item = (BallItem) gameObject;
                Vector2 newPosition = new Vector2(item.transform.position.x, item.transform.position.y);
                newPosition.y += Coordinates.BRICK_HEIGHT + Coordinates.BRICK_MARGIN;
                item.setPosition(newPosition);
                item.animateMove();
            }
        }

        if (maxLevel == 10) {
            scene.gameStatus = Scene.END_GAME;
        }

        scene.scheduler.postDelayed(100, () -> {
                    Random random = new Random();

                    List<Integer> itemIndexList = new ArrayList<>();
                    for (int i = 0; i < 6; i++) itemIndexList.add(i);
                    Collections.shuffle(itemIndexList);

                    int itemIndex = itemIndexList.get(itemIndexList.size() - 1);
                    itemIndexList = itemIndexList.subList(0, random.nextInt(3) + 2);

                    for (int index : itemIndexList) {
                        Vector2 vector2 = new Vector2();
                        vector2.x = (float) index * (Coordinates.BRICK_WIDTH + Coordinates.BRICK_MARGIN) + (Coordinates.BRICK_WIDTH + Coordinates.BRICK_MARGIN + Coordinates.GAME_FRAME_STROKE) / 2f;
                        vector2.y = (float) Coordinates.BRICK_HEIGHT / 2f + scene.frameMarginTop + Coordinates.GAME_FRAME_STROKE;
                        Transform transform = new Transform();
                        transform.translate(vector2);
                        Brick brick = new Brick(transform.position, scene.level);
                        scene.gameObjectList.add(brick);
                    }


                    Vector2 itemCenter = new Vector2(
                            (float) itemIndex * (Coordinates.BRICK_WIDTH + Coordinates.BRICK_MARGIN) + (Coordinates.BRICK_WIDTH + Coordinates.BRICK_MARGIN) / 2f + Coordinates.GAME_FRAME_STROKE,
                            (float) Coordinates.BRICK_HEIGHT / 2f + scene.frameMarginTop + Coordinates.GAME_FRAME_STROKE
                    );
                    BallItem item = new BallItem(itemCenter);
                    scene.gameObjectList.add(item);


                }
        );
    }
}
