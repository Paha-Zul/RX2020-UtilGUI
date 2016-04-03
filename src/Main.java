import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    int descNumRow = 0;
    int choiceRow = 0;
    int choiceCol = 0;
    int outcomeCounter = 0;
    int actionAreaCounter = 0;

    GridPane mainGrid = new GridPane();
    GridPane descGrid = new GridPane();

    GridPane choiceGrid = new GridPane();
    GridPane choiceTextGrid = new GridPane();

    GridPane actionAreaGrid = new GridPane();

    Controller controller = new Controller();

    public void resetGrids(){
        descGrid = new GridPane();

        choiceGrid = new GridPane();
        choiceTextGrid = new GridPane();

        actionAreaGrid = new GridPane();

        descNumRow = 0;
        choiceRow = 0;
        choiceCol = 0;
        outcomeCounter = 0;
        actionAreaCounter=0;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        new EventMode().start(primaryStage);

    }


    public static void main(String[] args) {
        launch(args);
    }

}
