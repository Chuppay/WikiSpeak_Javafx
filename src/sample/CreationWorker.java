package sample;

import javafx.application.Platform;

import java.io.File;
import java.io.FileWriter;

public class CreationWorker  extends Thread{

    private String _searchTerm;
    private String _creationName;
    private String[] _info;

    public CreationWorker(String searchTerm, String creationName,String[] info){
        _searchTerm=searchTerm;
        _creationName=creationName;
        _info=info;
    }

    @Override
    public void run(){
        try {
            String home = System.getProperty("user.home");
            String sep = System.getProperty("file.separator");

            String dir = home + sep + "Documents"+sep+"WikiSpeak" + sep + _creationName + sep;

            String command = "mkdir " + _creationName + " ; ";
            ProcessBuilder createCreation = new ProcessBuilder("bash", "-c", command);
            createCreation.directory(new File(home + sep + "Documents" + sep +"WikiSpeak"));
            Process process = createCreation.start();
            process.waitFor();

            File file = new File(dir + _searchTerm+".txt");
            file.createNewFile();
            FileWriter fw = new FileWriter(dir + _searchTerm+".txt");
            for (int i =0;i<_info.length;i++){
                fw.write(_info[i] + ". ");
            }
            fw.close();

            File script = new File(dir+"Script.sh");
            script.createNewFile();
            FileWriter scriptWriter = new FileWriter(dir+"Script.sh");
            scriptWriter.write("#!/bin/bash \n");
            scriptWriter.write("cat  '" + dir + _searchTerm+".txt' | text2wave -o " + dir + _searchTerm+".wav \n" );
            scriptWriter.write("LENGTH=\"soxi -D " + dir + _searchTerm+".wav\"\n");
            scriptWriter.write("ffmpeg -f lavfi -i color=c=blue:s=500x500:d=`$LENGTH` -vf \"\n" +
                    "drawtext=fontfile=*.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" +
                    _searchTerm + "\" " + dir + _searchTerm+"vid.mp4 ; \n");
            scriptWriter.write("ffmpeg -i "+ dir + _searchTerm+"vid.mp4 -i " + dir + _searchTerm+".wav -strict -2 " +
                    dir +"creation.mp4 \n");
            scriptWriter.close();

            Process p = Runtime.getRuntime().exec("bash "+dir+"Script.sh");

            Runnable done = () -> {
                popUpWindow.popUP("Sucess","Creation successful!");
            };
            Platform.runLater(done);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
