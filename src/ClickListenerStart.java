import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClickListenerStart implements ActionListener
{
    private int numClicks = 0;

    public void actionPerformed(ActionEvent event)
    {
        numClicks++;
        System.out.println("I was clicked " + numClicks + " times.");
    }
}