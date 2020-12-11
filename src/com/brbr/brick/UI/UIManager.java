package com.brbr.brick.UI;

import com.brbr.brick.InputData;
import com.brbr.brick.Scene;
import com.brbr.brick.debug.Debugger;
import com.brbr.brick.math.Vector2;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.brbr.brick.GameManager.GAME_HEIGHT;
import static com.brbr.brick.GameManager.GAME_WIDTH;

public class UIManager { // UI들을 관리하는 클래스

    private Scene scene;
    private static UIManager instance = null;
    private List<UILayer> layerList = new ArrayList();

    private UILayer beforeLayer;
    private UILayer pauseLayer;
    private UILayer proceedingLayer;
    private UILayer endLayer;

    private TextUI scoreUI;
    private TextUI recordUI;
    private TextUI endRecordUI;
    private TextUI endScoreUI;
    private ButtonUI pauseButton;

    public static UIManager getInstance(Scene scene) { // 싱글턴 패턴을 이용하여 구현
        if (instance == null) instance = new UIManager(scene);
        return instance;
    }

    public static UIManager getInstance() {
        if (instance == null) throw new NullPointerException();
        return instance;
    }

    public UIManager(Scene scene) {
        this.scene = scene;
    }

    public void init() {
        setProceedingUI();
        setBeforeUI();
        setPauseUI();
        setEndUI();
    }

    private void setBeforeUI() { // 게임 시작 전 화면의 UI Layer를 세팅해준다.
        beforeLayer = new UILayer();

        Color backgroundColor = new Color(251, 136, 54);
        Color textColor = Color.WHITE;

        ButtonUI startButton = new ButtonUI("Game Start", new Vector2(GAME_WIDTH / 2f - 100, GAME_HEIGHT / 2f - 55),
                20, 200, 50);
        startButton.setBackgroundColor(backgroundColor);
        startButton.setTextColor(textColor);

        ButtonUI quitButton = new ButtonUI("Quit", new Vector2(GAME_WIDTH / 2f - 100, GAME_HEIGHT / 2f),
                20, 200, 50);
        quitButton.setBackgroundColor(backgroundColor);
        quitButton.setTextColor(textColor);

        startButton.setButtonClickCallback(() -> {
            scene.gameStatus = Scene.PROCEEDING_GAME;
            scene.needToShoot = true;
            scene.needLevelUpdate = true;
        });

        quitButton.setButtonClickCallback(() -> System.exit(0));

        beforeLayer.addButtonUI(startButton);
        beforeLayer.addButtonUI(quitButton);

        beforeLayer.background = true;

        addLayer(beforeLayer);
    }

    private void setProceedingUI() { // 게임 진행중인 화면의 UI Layer를 세팅해준다.
        proceedingLayer = new UILayer();
        recordUI = new TextUI("RECORD: " + scene.scoreManager.record,
                new Vector2(scene.frameWidth / 2f - 48, 30), 20);
        scoreUI = new TextUI("SCORE: 0", new Vector2(scene.frameWidth / 2f - 48, 60), 20);

        ButtonUI pauseButton = new ButtonUI("Ⅱ", new Vector2(scene.frameWidth - 50, 15),
                20, 40, 40);
        pauseButton.setButtonClickCallback(() -> scene.gameStatus = Scene.PAUSE_GAME);

        proceedingLayer.addTextUI(recordUI);
        proceedingLayer.addTextUI(scoreUI);
        proceedingLayer.addButtonUI(pauseButton);

        addLayer(proceedingLayer);
    }

    private void setPauseUI() { // 일시정지 화면의 UI Layer를 세팅해준다
        pauseLayer = new UILayer();

        Color backgroundColor = new Color(251, 136, 54);
        Color textColor = Color.WHITE;

        ButtonUI resumeButton = new ButtonUI("resume", new Vector2(GAME_WIDTH / 2f - 90, GAME_HEIGHT / 2f - 40),
                20, 180, 50);

        resumeButton.setButtonClickCallback(() -> scene.gameStatus = Scene.PROCEEDING_GAME);
        resumeButton.setBackgroundColor(backgroundColor);
        resumeButton.setTextColor(textColor);

        pauseLayer.addButtonUI(resumeButton);

        pauseLayer.background = true;

        addLayer(pauseLayer);
    }

    private void setEndUI(){ // 게임 종료 화면의 UI Layer를 세팅해준다
        endLayer = new UILayer();
        Color backgroundColor = new Color(251, 136, 54);
        Color textColor = Color.WHITE;

        TextUI gameOverUI = new TextUI("Game Over",
                new Vector2(GAME_WIDTH / 2f - 120, scene.frameHeight / 2f - 50), 40);

        endRecordUI = new TextUI("RECORD: " + scene.scoreManager.record,
                new Vector2(GAME_WIDTH / 2f - 100, scene.frameHeight / 2f - 5), 20);
        endScoreUI = new TextUI("SCORE: " + scene.scoreManager.score, new Vector2(GAME_WIDTH / 2f - 100, scene.frameHeight / 2f + 20), 20);

        ButtonUI restartButton = new ButtonUI("Restart", new Vector2(GAME_WIDTH / 2f - 100, GAME_HEIGHT / 2f - 45),
                20, 200, 50);
        restartButton.setButtonClickCallback(() -> scene.gameStatus = Scene.RESET_GAME);
        restartButton.setBackgroundColor(backgroundColor);
        restartButton.setTextColor(textColor);

        ButtonUI quitButton = new ButtonUI("Quit", new Vector2(GAME_WIDTH / 2f - 100, GAME_HEIGHT / 2f+ 10),
                20, 200, 50);
        quitButton.setButtonClickCallback(() -> System.exit(0));
        quitButton.setBackgroundColor(backgroundColor);

        quitButton.setTextColor(textColor);

        endLayer.addTextUI(endRecordUI);
        endLayer.addTextUI(endScoreUI);
        endLayer.addTextUI(gameOverUI);
        endLayer.addButtonUI(restartButton);
        endLayer.addButtonUI(quitButton);
        endLayer.background = true;
        addLayer(endLayer);
    }

    private void addLayer(UILayer layer) {
        layerList.add(layer);
    } // 레이어를 LayerList에 추가해준다.

    public void drawUI(Graphics g, int gameWidth, int gameHeight) {
        // UI를 그려주고 Scene에서 gameStatus에 따라 Layer의 visible을 지정해준다
        scoreUI.setText("SCORE: " + scene.scoreManager.score);
        recordUI.setText("RECORD: " + scene.scoreManager.record);
        endScoreUI.setText("SCORE: " + scene.scoreManager.score);
        endRecordUI.setText("RECORD: " + scene.scoreManager.record);

        switch (scene.gameStatus) {
            case Scene.BEFORE_GAME -> {
                beforeLayer.setVisible(true);
                pauseLayer.setVisible(false);
                proceedingLayer.setClickUnable(true);
                endLayer.setVisible(false);
            }
            case Scene.PROCEEDING_GAME -> {
                beforeLayer.setVisible(false);
                pauseLayer.setVisible(false);
                proceedingLayer.setClickUnable(false);
                endLayer.setVisible(false);
            }
            case Scene.PAUSE_GAME -> {
                beforeLayer.setVisible(false);
                pauseLayer.setVisible(true);
                proceedingLayer.setClickUnable(true);
                endLayer.setVisible(false);
            }
            case Scene.END_GAME -> {
                beforeLayer.setVisible(false);
                pauseLayer.setVisible(false);
                proceedingLayer.setClickUnable(true);
                endLayer.setVisible(true);
            }
        }

        for (UILayer layer : layerList.stream().filter(layer -> layer.visible).collect(Collectors.toList())) {
            layer.drawUI(g, gameWidth, gameHeight);
        }
    }

    public boolean buttonClickCheck(int pointX, int pointY) {
        for (UILayer layer : layerList.stream().filter(layer -> !layer.clickUnable).collect(Collectors.toList())) {
            return layer.buttonClickCheck(pointX, pointY);
        }
        return false;
    }

    public boolean buttonClickCheck(InputData inputData) { // 직접 버튼을 구현하여 해당 레이어에서버튼이 클릭되었는지 체크해주는 메서드
        if (inputData.type != InputData.InputType.Click) return false;
        Debugger.Print("buttonclickcheck");
        int pointX = inputData.x;
        int pointY = inputData.y;

        for (UILayer layer : layerList.stream().filter(layer -> !layer.clickUnable).collect(Collectors.toList())) {
            return layer.buttonClickCheck(pointX, pointY);
        }
        return false;
    }
}
