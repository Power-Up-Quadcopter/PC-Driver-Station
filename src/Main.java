import Control.CommandHandler;
import Control.GUI_C;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class Main {

    public static void main(String[] args) {
        GUI_C.initialize();
        CommandHandler.initialize();


        Controller c;
        try {
            Controllers.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        Controllers.poll();

        for(int i = 0;i <  Controllers.getControllerCount(); i++) {
            c = Controllers.getController(i);
            System.out.println(c.getName()  );
        }

    }

}
