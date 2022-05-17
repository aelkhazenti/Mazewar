package main.framework.game.rooms;

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.framework.animation.SpriteAnimator;
import main.framework.game.PlayerProperties;
import main.framework.object2D.Character2D;
import main.framework.controller.Controller;
import main.framework.controller.Mover;
import main.framework.object2D.GameObject2D;
import main.framework.object2D.Hotspot;
import main.framework.state.IState;
import main.framework.state.StateStack;
import java.io.*;
import java.util.Date;
import java.util.Timer;

import java.util.ArrayList;

import static main.framework.game.Game.root;

/*
 * Sample game level
 */


public class Room1 implements IState {

    private String gokulvl = "normale";
    /**---------------------------------**/
    private final Scene scene;
    private final GraphicsContext graphicsContext;
    private PerspectiveCamera perspectiveCamera;
    private Stage dialog;
    private VBox dialogVbox = new VBox(20);
    private Text scoreAndTime = new Text("Score : "+this.score+" time : 0");
    private Scene dialogScene;
    private Timer timer = new Timer();
    /**---------------------------------**/

    /**--------------------------- game objects ----------------------------**/
    private Character2D player;
    private ArrayList<GameObject2D> sideWalls;
    private GameObject2D talker2D;
    private GameObject2D potion2D;
    private GameObject2D key2D;
    // hotspots
    private Hotspot enemy;
    private Hotspot talker;
    private Hotspot potion;
    private Hotspot key;
    private int score =0 ;
    // player properties
    private Controller playerController;
    private Mover playerMover;
    private ImageView imageView;

    // sprite
    Image tileset;
    Image playerSprite; // player sprite playerSprite
    Image otherSprites;
    Image potionItem;
    Image keyItem;

    //Goku Transformation
    Image gokuNormale;
    Image gokuSuperSaiyan;
    Image gokuSuperSaiyanGod;

    SpriteAnimator animator;
    /**---------------------------------------------------------------------**/

    public Room1(Scene scene, GraphicsContext graphicsContext) {
        this.scene = scene;
        this.graphicsContext = graphicsContext;

    }

    @Override
    public void init() {

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 2048, 2048);

        // set up camera
        perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.setTranslateZ(-1000);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFarClip(2000.0);
        perspectiveCamera.setFieldOfView(25);
        scene.setCamera(perspectiveCamera);

        // change canvas size
        graphicsContext.getCanvas().setHeight(1344);
        graphicsContext.getCanvas().setWidth(2101);


        // Initialize game objects
        // 2d characters and controller
//        player = new Character2D("player", /*w*/20, /*h*/20, /*x*/2000, /*y*/1200, 2); // always make x and y even
        player = new Character2D("player", /*w*/20, /*h*/20, /*x*/560, /*y*/560, 2); // always make x and y even
        playerController = new Controller(scene);
        playerMover = new Mover(playerController, player);

        // 2d game objects (objects with no collisions)
        sideWalls = new ArrayList<>();

        // invisible walls instantiation
        for(int x = 512; x <= 2080; x+= 32) {
            sideWalls.add(new GameObject2D("wall", 32, 32, x, 512));
        }
        for(int x = 512; x <= 2080; x+= 32) {
            sideWalls.add(new GameObject2D("wall", 32, 32, x, 1312));
        }
        for(int y = 512; y <= 1312; y+= 32) {
            sideWalls.add(new GameObject2D("wall", 32, 32, 512, y));
        }
        for(int y = 512; y <= 1312; y+= 32) {
            sideWalls.add(new GameObject2D("wall", 32, 32, 2080, y));
        }

        this.setMazeWalls();

//        talker2D = new GameObject2D("talker", 32, 32, 2048, 1280);
//        potion2D = new GameObject2D("talker",32,32,688,688);
//        player.addCollision(sideWalls);
//        player.addCollision(talker2D); // add a collision to talker
//        player.addCollision(potion2D);

        // hotspots and triggers
//        enemy = new Hotspot("door", 32, 32 , 768, 832); // combat initiator
//        talker = new Hotspot("talker", 64, 64, 2048, 1280); // dialogue initiator
//        enemy.addTriggerCharacter(player);
//        talker.addTriggerCharacter(player);

//        potion = new Hotspot("potion", 64, 64, 688, 688); // dialogue initiator
//        potion.addTriggerCharacter(player);

//        final Stage dialog = new Stage();


        dialog = new Stage();
        dialog.initModality(Modality.NONE);

        dialogVbox.getChildren().add(scoreAndTime);
        dialogScene = new Scene(dialogVbox, 200, 40);
        dialog.setScene(dialogScene);

        dialog.show();

        dialog.setX(0);
        dialog.setY(0);

//        scorePopup = new Popup();
//
//        this.scorelabel.setStyle(" -fx-background-color: white;");
//        scorePopup.getContent().add(this.scorelabel);
//
//        // set size of label
//        this.scorelabel.setMinWidth(80);
//        this.scorelabel.setMinHeight(50);
//        scorePopup.show();

        // set up images
        playerSprite = new Image(getClass().getResourceAsStream("../resources/sprites.png"));
        otherSprites = new Image(getClass().getResourceAsStream("../resources/EntitySet.png"));
        tileset = new Image(getClass().getResourceAsStream("../resources/tileset.png"));
        potionItem = new Image(getClass().getResourceAsStream("../resources/shield_plat.png"));

        gokuNormale = new Image(getClass().getResourceAsStream("../resources/goku.png"));
        gokuSuperSaiyan = new Image(getClass().getResourceAsStream("../resources/goku_super_saiyan.png"));
        gokuSuperSaiyanGod = new Image(getClass().getResourceAsStream("../resources/goku_super_saiyan_god.png"));

        try {
            keyItem = new Image(getClass().getResourceAsStream("../resources/key.png"));
        }catch (Exception e){
            System.out.println(e.getCause());
        }

        if (gokulvl.equals("normale")){

            imageView = new ImageView(otherSprites);
            imageView.setViewport(new Rectangle2D(0, 32, 32, 32));
            root.getChildren().add(imageView);
            // instantiates sprite animator
            animator = new SpriteAnimator( imageView,
                    Duration.millis(300), 3, 3, /* offsetX */ 0, /* offsetY */ 0, 32, 32
            );
            animator.setCycleCount(Animation.INDEFINITE);
        }


    }

    @Override
    public void onEnter() {
        // on enter draw this
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 2048, 2048);
//        hpAndTime.fillText("score : "+this.score+" . time : ",512,512,40);
        scoreAndTime.setText("score : "+this.score+" . time : ");
        player = PlayerProperties.Player1.getCharacter2D();
        playerController = new Controller(scene);
        playerMover = new Mover(playerController, player);

        animator.play();
    }

    @Override
    public void update(long currentTime) {
        animator.update(this.playerMover, 0, 32, 64, 96);
        animator.updateView(playerMover);
        playerMover.update();
        perspectiveCamera.setTranslateY(player.getY());
        perspectiveCamera.setTranslateX(player.getX());

        if (enemy != null) {
            if (enemy.isCharacterOnHotspot()) {
                System.out.println("Player is standing on door!");
                StateStack.push("combat");
                enemy = null;
                gokuTransformation(gokuSuperSaiyanGod);
                gokulvl = "gokuSuperSaiyanGod";
                score+=100;
                onExit();
            }
        }

        if (potion!=null){

            if (potion.isCharacterOnHotspot() ){
                System.out.println("Player is standing on potion!");
                StateStack.push("potion");
                potion =null;
                gokuTransformation(gokuSuperSaiyan);
                gokulvl = "gokuNormale";
                player.setName("goku");
                score+=100;
                onExit();
            }
        }

        if (key!=null){

            if(key.isCharacterOnHotspot()){
                System.out.println("player is standing on key");
                StateStack.push("key");
                key=null;
                gokuTransformation(gokuSuperSaiyan);
                gokulvl = "gokuSuperSaiyan";
                System.out.println(player.getName());
                score+=100;
                onExit();
            }
        }

        if(playerController.getInputs().contains("ESCAPE")) {
            onExit();
            StateStack.push("gameMenu");
        }

        scoreAndTime.setText("score : "+this.score+" . time : ");
    }

    @Override
    public void draw() {

        // draw grass floor
        for (int y = 512; y <= 1536; y += 32) {
            for (int x = 512; x <= 2101; x += 32) {
                graphicsContext.drawImage(tileset, 0, 0, 32, 32, x, y, 32, 32);
            }
        }

        // draw walls
        for (GameObject2D wall : sideWalls) {
            graphicsContext.drawImage(tileset, 32, 0, 32, 32, wall.getX(), wall.getY(), wall.getHeight(), wall.getWidth());
        }

        // combat initiator hotspot
        if(enemy != null)
            graphicsContext.drawImage(otherSprites, 128, 0, 32, 32, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());

        graphicsContext.drawImage(playerSprite, 192, 224, 32, 32, talker2D.getX(), talker2D.getY(), talker2D.getWidth(), talker2D.getHeight());

        graphicsContext.setFill((talker.isCharacterOnHotspot()? new Color(1, 0, 0, 0.3) : new Color(1, 1, 0, 0.3)));
        graphicsContext.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());


//        graphicsContext.drawImage(potionItem, 0, 0, 32, 32, 688, 688, 32, 32);
        if (potion!=null)
            graphicsContext.drawImage(potionItem, 0, 0, 32, 32, potion2D.getX(), potion2D.getY(), potion2D.getWidth(), potion2D.getHeight());


        if (key!=null)
            graphicsContext.drawImage(keyItem, 0, 0, 32, 32, key2D.getX(), key2D.getY(), key2D.getWidth(), key2D.getHeight());
//        key =
    }


    @Override
    public void onExit() {
        scene.setOnKeyPressed(null);
        scene.setOnKeyReleased(null);
        animator.stop();
        // set global player properties
        PlayerProperties.Player1.setCharacter2D(player);
    }

    @Override
    public void onClose() {
        imageView.setImage(null);
        animator = null;
    }



    public void setMazeWalls(){
        File file = new File("src/main/framework/game/rooms/mazeWall2");
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            String[] tab;
            int x = 544;
            int y = 544;
            while ((st = br.readLine()) != null){
                tab = st.split("");
                for (int i = 0; i < tab.length; i++) {
                    if (tab[i].equals("1")){
                        sideWalls.add(new GameObject2D("wall", 32, 32, x, y));
                    }

                    switch (tab[i]){
                        case "1":
                            sideWalls.add(new GameObject2D("wall", 32, 32, x, y));
                            break;
                        case "2":
                            enemy = new Hotspot("door", 32, 32 , x, y); // combat initiator
                            enemy.addTriggerCharacter(player);

                            break;
                        case "3":
                            talker = new Hotspot("talker", 64, 64, x, y); // dialogue initiator
                            talker.addTriggerCharacter(player);
                            talker2D = new GameObject2D("talker", 32, 32, x, y);

                            player.addCollision(talker2D); // add a collision to talker
                            break;
                        case"4":
                            potion2D = new GameObject2D("postion",32,32,x,y);

                            potion = new Hotspot("potion", 64, 64, x, y); // dialogue initiator
                            potion.addTriggerCharacter(player);
                            System.out.println(x+"---potion--"+y);
                            break;
                        case"5":
                            key2D = new GameObject2D("key",32,32, x, y);
                            key = new Hotspot("key",64,64, x, y);
                            key.addTriggerCharacter(player);
                            System.out.println(x+"---------------key"+y);
                            break;

                    }
                    x+=32;
                }

                x=544;
                y+=32;
            }
            player.addCollision(sideWalls);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void gokuTransformation(Image GokuImage){

        imageView.setImage(null);

        imageView = new ImageView(GokuImage);
        imageView.setViewport(new Rectangle2D(32, 32, 32, 32));
        root.getChildren().add(imageView);

        // instantiates sprite animator
        animator = new SpriteAnimator( imageView,
                Duration.millis(300), 3, 3, /* offsetX */ 0, /* offsetY */ 0, 32, 32
        );


    }

    public void setTimer(){
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < 2*60*1000) {
            //perform db poll/check
            elapsedTime = (new Date()).getTime() - startTime;
        }
    }
}