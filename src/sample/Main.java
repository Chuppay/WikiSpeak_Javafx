package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sun.plugin.javascript.navig.Anchor;


import java.io.*;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private Stage _stage;
    private Scene _mainMenu;
    private Scene _creationScene;
    private Scene _videoScene;
    private Scene _listScene;
    private List<String> _creations;
    private String _home;
    private String _sep;

    @Override
    public void start(Stage primaryStage) throws Exception{
        _stage = primaryStage;
        _stage.setTitle("WikiSpeak");

        intialise();

        _stage.setScene(_mainMenu);
        _stage.show();
    }

    public void intialise(){
        _home = System.getProperty("user.home");
        _creations = new ArrayList<String>();
        _sep = System.getProperty("file.separator");
        try{
            ProcessBuilder makeDirectory = new ProcessBuilder("mkdir","WikiSpeak");
            makeDirectory.directory(new File(_home + _sep + "Documents"));
            Process makeDirectoryProcess = makeDirectory.start();

            String command = "ls -d */ | cut -d'/' -f 1";
            ProcessBuilder scanForCreations = new ProcessBuilder("bash", "-c", command );
            scanForCreations.directory(new File(_home + _sep +"Documents"+_sep+"WikiSpeak"));
            Process scanForCreationsProcess = scanForCreations.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(scanForCreationsProcess.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(scanForCreationsProcess.getErrorStream()));

            int exitStatus = scanForCreationsProcess.waitFor();
            String line;

            if (exitStatus == 0) {
                while ((line = stdout.readLine()) != null) {
                    _creations.add(line);
                }
            } else {
                while ((line = stderr.readLine()) != null) {
                    System.err.println(line);
                }
            }

            System.out.println(_creations);

        } catch (Exception e){
            e.printStackTrace();
        }
        createMainMenuScene();
        createCreationScene();
        createListScene();
    }

    public void createMainMenuScene() {
        VBox midBox = new VBox();
        midBox.setAlignment(Pos.CENTER);

        Button btnCreate = new Button("Create creations");
        btnCreate.setPrefSize(200, 30);
        btnCreate.setOnAction(e -> _stage.setScene(_creationScene));

        Button btnList = new Button("List creations");
        btnList.setPrefSize(200, 30);
        btnList.setOnAction(e -> _stage.setScene(_listScene));

        Text select = new Text("Please choose an option:");
        select.setFont(Font.font("Ariel",20));

        midBox.setSpacing(50);
        midBox.getChildren().addAll(select,btnCreate,btnList);

        BorderPane outerBox= new BorderPane();

        Text welcome = new Text("Welcome to WikiSpeak");
        welcome.setFont(Font.font("Ariel",35));
        VBox topBox = new VBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().addAll(welcome);

        outerBox.setTop(topBox);
        outerBox.setCenter(midBox);

        _mainMenu = new Scene(outerBox, 500, 500);
    }

    public void createCreationScene(){
        AnchorPane layout = new AnchorPane();

        Text prompt = new Text("Create creations");
        AnchorPane.setLeftAnchor(prompt,50.0);
        AnchorPane.setTopAnchor(prompt,45.0);
        prompt.setFont(Font.font("Ariel",25));

        Button back = new Button("Back");
        AnchorPane.setLeftAnchor(back,50.0);
        AnchorPane.setBottomAnchor(back,50.0);
        back.setPrefSize(100,20);
        back.setOnAction(e -> _stage.setScene(_mainMenu));

        Text searchTermPrompt = new Text("Enter what you want to search:");
        AnchorPane.setLeftAnchor(searchTermPrompt,50.0);
        AnchorPane.setTopAnchor(searchTermPrompt,160.0);
        searchTermPrompt.setFont(Font.font("Ariel",15));

        TextField searchTerm = new TextField();
        searchTerm.setPrefColumnCount(30);
        AnchorPane.setLeftAnchor(searchTerm,50.0);
        AnchorPane.setTopAnchor(searchTerm,180.0);

        Text creationNamePrompt = new Text("Enter name of creation:");
        AnchorPane.setLeftAnchor(creationNamePrompt,50.0);
        AnchorPane.setTopAnchor(creationNamePrompt,220.0);
        creationNamePrompt.setFont(Font.font("Ariel",15));

        TextField creationName = new TextField();
        creationName.setPrefColumnCount(30);
        AnchorPane.setLeftAnchor(creationName,50.0);
        AnchorPane.setTopAnchor(creationName,240.0);

        Button create = new Button("Create");
        AnchorPane.setLeftAnchor(create,50.0);
        AnchorPane.setBottomAnchor(create,150.0);
        create.setPrefSize(100,20);
        create.setOnAction(e -> {
            processCreation(searchTerm.getCharacters().toString(), creationName.getCharacters().toString());
        });

        layout.getChildren().addAll(prompt,back,creationName,searchTerm,
                create,creationNamePrompt,searchTermPrompt);
        _creationScene = new Scene(layout,500,500);
    }

    public void createListScene() {
        AnchorPane layout = new AnchorPane();

        Text prompt = new Text("List creations");
        AnchorPane.setLeftAnchor(prompt,50.0);
        AnchorPane.setTopAnchor(prompt,45.0);
        prompt.setFont(Font.font("Ariel",30));

        Text select = new Text("Select an option:");
        AnchorPane.setLeftAnchor(select,320.0);
        AnchorPane.setTopAnchor(select,100.0);
        prompt.setFont(Font.font("Ariel",15));

        ComboBox<String> dropDown = new ComboBox();
        AnchorPane.setLeftAnchor(dropDown,50.0);
        AnchorPane.setTopAnchor(dropDown,150.0);
        dropDown.setPromptText("Your creations");

        Button play = new Button("Play");
        AnchorPane.setLeftAnchor(play,325.0);
        AnchorPane.setTopAnchor(play,150.0);
        play.setPrefSize(100,20);
        play.setOnAction(e -> playCreation(dropDown.getValue()));

        Button delete = new Button("Delete");
        AnchorPane.setLeftAnchor(delete,325.0);
        AnchorPane.setTopAnchor(delete,200.0);
        delete.setPrefSize(100,20);
        delete.setOnAction(e -> deleteCreation(dropDown.getValue()));

        Button back = new Button("Back");
        AnchorPane.setLeftAnchor(back,50.0);
        AnchorPane.setBottomAnchor(back,50.0);
        back.setPrefSize(100,20);
        back.setOnAction(e -> _stage.setScene(_mainMenu));


        dropDown.getItems().addAll(_creations);
        layout.getChildren().addAll(prompt,dropDown,select,play,delete,back);

        _listScene = new Scene(layout,500,500);
    }

    private void playCreation(String creation) {
        if (creation == null){
            popUpWindow.popUP("ERROR","Please select a creation");
            return;
        }

        AnchorPane layout = new AnchorPane();

        File url = new File(_home+_sep+"Documents"+_sep+"WikiSpeak"+_sep+creation+_sep+"creation.mp4");
        Media video = new Media(url.toURI().toString());
        MediaPlayer player = new MediaPlayer(video);
        player.setAutoPlay(true);
        MediaView mediaView = new MediaView(player);

        AnchorPane.setTopAnchor(mediaView,0.0);
        AnchorPane.setLeftAnchor(mediaView,0.0);
        AnchorPane.setRightAnchor(mediaView,0.0);
        AnchorPane.setBottomAnchor(mediaView,100.0);

        Button back = new Button("Back");
        AnchorPane.setLeftAnchor(back,50.0);
        AnchorPane.setBottomAnchor(back,50.0);
        back.setPrefSize(100,20);
        back.setOnAction(e -> {
            player.stop();
            _stage.setScene(_mainMenu);
        });

        Button pp = new Button("Play/Pause");
        AnchorPane.setRightAnchor(pp,50.0);
        AnchorPane.setBottomAnchor(pp,50.0);
        pp.setPrefSize(100,20);
        pp.setOnAction(e -> {
            if (player.getStatus() == MediaPlayer.Status.PAUSED){
                player.play();
            } else {
                player.pause();
            }
        });

        layout.getChildren().addAll(mediaView,pp,back);

        Scene scene = new Scene(layout,500,600);
        _stage.setScene(scene);

    }

    private void deleteCreation(String creation) {
        if (creation == null) {
            popUpWindow.popUP("ERROR!", "Please select a creation");
            return;
        }

        boolean confirmDelete = popUpWindow.confirm("", "Are you sure you want to delete this creation?");
        if (confirmDelete) {
            try {
                String command = "rm -r " + creation;
                ProcessBuilder deleteCreation = new ProcessBuilder("bash", "-c", command);
                deleteCreation.directory(new File(_home + _sep + "Documents" + _sep + "WikiSpeak"));
                Process deleteCreationProcess = deleteCreation.start();
                popUpWindow.popUP("File deleted", creation + " has been deleted.");
                start(_stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            _stage.setScene(_mainMenu);
        }
    }

    public void processCreation(String searchTerm, String creationName) {
        System.out.println("Search: " + searchTerm + "\nCreation: " + creationName);

        try {
            ProcessBuilder wikit = new ProcessBuilder("wikit", searchTerm);
            wikit.directory(new File(_home + "/Documents"));
            Process wikitProcess = wikit.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(wikitProcess.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(wikitProcess.getErrorStream()));

            int exitStatus = wikitProcess.waitFor();
            String info = stdout.readLine();

            if (info.equals(searchTerm + " not found :^(")){
                popUpWindow.popUP("ERROR","Search term not found.");
                return;
            }

            String[] userPref = popUpWindow.getSentences(info);

            if (exitStatus == 1) {
            }
                while ((info = stderr.readLine()) != null) {
                    System.err.println(info);
            }

            if (Pattern.compile("[*<>\\\\|\"_^/:]").matcher(creationName).find()) {
                popUpWindow.popUP("ERROR", "Please choose a name without any of the following characters\n" +
                        "\\ / : * \" < > | ");
            } else if (_creations.contains(creationName)) {
                boolean override = popUpWindow.confirm("ERROR", "File name already exists. " +
                        "Would you like to override?");
                if (!override) {
                    System.out.println("not overridden");
                    return;
                } else {
                    String command = "rm -r " + creationName;
                    ProcessBuilder deleteCreation = new ProcessBuilder("bash", "-c", command);
                    deleteCreation.directory(new File(_home + "Documents/WikiSpeak"));
                    popUpWindow.popUP("Overridden", creationName + " has been overridden.");
                    createCreation(searchTerm,creationName,userPref);
                }
            }
            createCreation(searchTerm,creationName,userPref);
            start(_stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCreation(String searchTerm, String creationName,String[] info) {
        try {
            String dir = _home + _sep + "Documents"+_sep+"WikiSpeak" + _sep + creationName + _sep;

            String command = "mkdir " + creationName + " ; ";
            ProcessBuilder createCreation = new ProcessBuilder("bash", "-c", command);
            createCreation.directory(new File(_home + _sep + "Documents" + _sep +"WikiSpeak"));
            Process process = createCreation.start();
            process.waitFor();

            File file = new File(dir + searchTerm+".txt");
            file.createNewFile();
            FileWriter fw = new FileWriter(dir + searchTerm+".txt");
            for (int i =0;i<info.length;i++){
                fw.write(info[i] + ". ");
            }
            fw.close();

            File script = new File(dir+"Script.sh");
            script.createNewFile();
            FileWriter scriptWriter = new FileWriter(dir+"Script.sh");
            scriptWriter.write("#!/bin/bash \n");
            scriptWriter.write("cat  '" + dir + searchTerm+".txt' | text2wave -o " + dir + searchTerm+".wav \n" );
            scriptWriter.write("LENGTH=\"soxi -D " + dir + searchTerm+".wav\"\n");
            scriptWriter.write("ffmpeg -f lavfi -i color=c=blue:s=500x500:d=`$LENGTH` -vf \"\n" +
                    "drawtext=fontfile=*.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" +
                    searchTerm + "\" " + dir + searchTerm+"vid.mp4 ; \n");
            scriptWriter.write("ffmpeg -i "+ dir + searchTerm+"vid.mp4 -i " + dir + searchTerm+".wav -strict -2 " +
                    dir +"creation.mp4 \n");
            scriptWriter.close();

            Process p = Runtime.getRuntime().exec("bash "+dir+"Script.sh");
            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
