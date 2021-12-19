import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClickListenerStop implements ActionListener
{
    public void actionPerformed(ActionEvent event)
    {
        String destFolder="C:\\Users\\ADMIN\\Desktop\\WebPages";
        String cmdPrompt = "cmd";
        String path="/c";
        String npmStop = "npx kill-port 8080";
        String npmStart = "npm start";
        File jsFile = new File(destFolder);

        List<String> startCommand=new ArrayList<String>();
        startCommand.add(cmdPrompt);
        startCommand.add(path);
        startCommand.add(npmStop);
        int result = 0;
        try {
            result = runExecution(startCommand,jsFile);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(result);

    }

    public int runExecution(List<String> command, File navigatePath) throws IOException, InterruptedException {
        System.out.println(command);

        ProcessBuilder executeProcess = new ProcessBuilder(command);
        executeProcess.directory(navigatePath);
        Process resultExecution = executeProcess.start();


        BufferedReader br = new BufferedReader(new InputStreamReader(resultExecution.getInputStream()));
        StringBuffer sb=new StringBuffer();

        String line;
        while((line=br.readLine())!=null){
            sb.append(line+System.getProperty("line.separator"));
        }
        br.close();
        int resultStatus ;
        resultStatus = resultExecution.waitFor();
        return resultStatus;

    }

}