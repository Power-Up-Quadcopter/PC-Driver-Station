import javax.swing.*;
import java.awt.*;

public class Main
{

    public static void main(String[] args)
    {
        Compatibles.setupGUI();

        UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTab.contentMargins", new Insets(10, 500, 0, 0));
        MainWindow mainWindow = new MainWindow();

    }

}
