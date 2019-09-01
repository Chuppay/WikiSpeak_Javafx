package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class popUpWindow {

    private Stage _stage;
    private static boolean _confirm;
    private static String[] _modInfo;

    public static void popUP(String name, String msg){
        Stage _stage = new Stage();

        _stage.initModality(Modality.APPLICATION_MODAL);
        _stage.setTitle(name);

        Label message = new Label(msg);

        Button close = new Button("Ok");
        close.setOnAction(e -> _stage.close());

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(message,close);

        Scene scene = new Scene(layout, 500,200);
        _stage.setScene(scene);
        _stage.showAndWait();
    }

    public static boolean confirm(String name, String msg) {
        Stage _stage = new Stage();

        _stage.initModality(Modality.APPLICATION_MODAL);
        _stage.setTitle(name);

        Label message = new Label(msg);

        Button yes = new Button("Yes");
        yes.setOnAction(e ->{
            _confirm = true;
            _stage.close();
        });

        Button no = new Button("No");
        no.setOnAction(e -> {
            _confirm = false;
            _stage.close();
        });

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(message,yes,no);

        Scene scene = new Scene(layout, 500,200);
        _stage.setScene(scene);
        _stage.showAndWait();

        return _confirm;
    }

    public static String[] getSentences(String info){
        Stage _stage = new Stage();
        _stage.initModality(Modality.APPLICATION_MODAL);
        _stage.setTitle("Get Sentences");

        String[] separateSentences = info.split("\\. ");

        ComboBox<String> select = new ComboBox<>();
        List<String> temp = new ArrayList<>();
        Label text = new Label();

        for(int count=0; count < separateSentences.length;count++){
            temp.add(Integer.toString(count + 1) + ". " + separateSentences[count]);
        }

        select.getItems().addAll(temp);
        select.setMaxWidth(500);

        Label message = new Label("Please select number of sentences up to and including: ");

        Button ok = new Button("Ok");
        ok.setOnAction(e ->{
            int numSentences = Integer.parseInt(select.getValue().substring(0,1));
            _modInfo = Arrays.copyOfRange(separateSentences,0,numSentences -1);
            _stage.close();
        });


        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(message,select,ok);

        Scene scene = new Scene(layout, 500,200);
        _stage.setScene(scene);
        _stage.showAndWait();

        return _modInfo;
    }
}
