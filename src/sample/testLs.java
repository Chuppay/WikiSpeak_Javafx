package sample;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class testLs {

    public static void main(String[] args) {

        try {
            ProcessBuilder pb = new ProcessBuilder("ls", "-a");
            List<String> output = new ArrayList();

            Process process = pb.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            int count = 1;
            String line;
            while ((line = stdout.readLine()) != null) {
                System.out.println("Number " + count + " output is " + line);
                output.add(line);
                count ++;
            }

            System.out.println(output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
