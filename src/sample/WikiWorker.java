package sample;

import javafx.application.Platform;

import javax.smartcardio.TerminalFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class WikiWorker extends Thread {

    private String _searchTerm;
    private String _info;

    public WikiWorker(String searchTerm) {
        _searchTerm = searchTerm;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder wikit = new ProcessBuilder("wikit", _searchTerm);
            wikit.directory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Documents"));
            Process wikitProcess = wikit.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(wikitProcess.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(wikitProcess.getErrorStream()));

            wikitProcess.waitFor();
            _info = stdout.readLine();

            if (_info.equals(_searchTerm + " not found :^(")) {
                Runnable popUp = () -> {
                    popUpWindow.popUP("ERROR", "Search term not found.");
                };
                Platform.runLater(popUp);
                _info = "";
            }

            System.out.println("ay we up in this bitch");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getInfo(){
        return _info;
    }
}
