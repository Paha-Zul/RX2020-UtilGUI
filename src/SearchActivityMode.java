import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Paha on 4/2/2016.
 */
public class SearchActivityMode {
    Stage primaryStage;
    GridPane mainGrid = new GridPane();
    GridPane actionAreaGrid = new GridPane();

    int actionAreaCounter = 0;

    ActivityController controller = new ActivityController();

    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("GUI");

        mainGrid.setAlignment(Pos.TOP_LEFT);
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setPadding(new Insets(25, 25, 25, 25));

        ScrollPane sp = new ScrollPane();
        sp.setContent(mainGrid);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Scene scene = new Scene(sp, 400, 600);
        primaryStage.setScene(scene);

        primaryStage.show();

        addStuff();
    }

    public void addStuff(){
        controller.setFile(new File(new File("").getAbsolutePath()+"/"+"searchActivities.json"));
        controller.loadSearchActivities();

        Button switchButton = new Button("Switch");

        Text scenetitle = new Text("Search Activities");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        mainGrid.add(scenetitle, 0, 0, 2, 1);
        mainGrid.add(switchButton, 0, 1);

        GridPane buttonGrid = new GridPane();

        Button saveButton = new Button("Save");
        Button clearButton = new Button("Clear");
        Button loadButton = new Button("Load");
        ObservableList<String> list = FXCollections.observableArrayList(asSortedList(controller.activityMap.keySet()));
        controller.activityComboBox = new ComboBox<>(list);

        buttonGrid.add(saveButton, 0, 0);
        buttonGrid.add(clearButton, 1, 0);
        buttonGrid.add(loadButton, 2, 0);
        buttonGrid.add(controller.activityComboBox, 3, 0);

        Label nameLabel = new Label("Name:");
        controller.nameField = new TextField();
        controller.nameField.setMaxWidth(200);

        Label buttonTitleLabel = new Label("Button Title:");
        controller.buttonTitleField = new TextField();
        controller.buttonTitleField.setMaxWidth(200);

        Label descLabel = new Label("Description:");
        controller.descTextArea = new TextArea();
        controller.descTextArea.setMaxWidth(200);
        controller.descTextArea.setWrapText(true);

        Label actionsLabel = new Label("Actions");
        Button lessButton = new Button("-");
        Button moreButton = new Button("+");

        actionAreaGrid.add(actionsLabel, 0, 0);
        actionAreaGrid.add(lessButton, 1, 0);
        actionAreaGrid.add(moreButton, 2, 0);

        mainGrid.add(buttonGrid, 0, 2);
        mainGrid.add(nameLabel, 0, 3);
        mainGrid.add(controller.nameField, 0, 4);
        mainGrid.add(buttonTitleLabel, 0, 5);
        mainGrid.add(controller.buttonTitleField, 0, 6);
        mainGrid.add(descLabel, 0, 7);
        mainGrid.add(controller.descTextArea, 0, 8);
        mainGrid.add(actionAreaGrid, 0, 9);

        moreButton.setOnAction(e -> addAnotherAction());
        lessButton.setOnAction(e -> removeAction());

        saveButton.setOnAction(ae -> controller.save());
        clearButton.setOnAction(ae -> reset());
        loadButton.setOnAction(ae -> loadActivityIntoGUI());

        switchButton.setOnAction(e -> {clean();
            try {
                new EventMode().start(primaryStage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    public void clean(){
        mainGrid.getChildren().clear();
        actionAreaGrid = new GridPane();
        actionAreaCounter = 0;
    }

    void reset(){
        clean();
        addStuff();
    }

    public void loadActivityIntoGUI(){
        //We need to get the activity before we clear the GUI.
        ActivityController.SearchActivityJSON act = controller.activityMap.get(controller.activityComboBox.getValue());

        controller.actionList = new ArrayList<>();
        clean();

        controller.nameField.setText(act.name);
        controller.buttonTitleField.setText(act.buttonTitle);
        controller.descTextArea.setText(act.description);

        for (ArrayList<String> list : act.action) {
            GridPane[] grids = addAnotherAction();
            for (String param : list) {
                addAnotherActionTextField(grids[2]).setText(param);
            }
        }
    }

    public GridPane[] addAnotherAction(){
        controller.actionList.add(new ArrayList<>());

        GridPane actionGrid = new GridPane();
        GridPane labelGrid = new GridPane();
        GridPane fieldGrid = new GridPane();

        fieldGrid.setUserData(actionAreaCounter);

        Label label = new Label("Action:");

        Button lessButton = new Button("-");
        Button moreButton = new Button("+");

        labelGrid.add(label, 0, 0);
        labelGrid.add(lessButton, 1, 0);
        labelGrid.add(moreButton, 2, 0);

        moreButton.setOnAction(e -> addAnotherActionTextField(fieldGrid));
        lessButton.setOnAction(e -> removeActionTextField(fieldGrid));

        actionGrid.add(labelGrid, 0, 0);
        actionGrid.add(fieldGrid, 0, 1);

        actionAreaGrid.add(actionGrid, 0, ++actionAreaCounter, 99, 1);

        return new GridPane[]{actionGrid, labelGrid, fieldGrid};
    }

    private void removeAction(){
        ObservableList<Node> list = actionAreaGrid.getChildren();
        if(list.size() > 3) {
            list.remove(list.size() - 1);
            controller.actionList.remove(controller.actionList.size()-1);
            actionAreaCounter--;
        }
    }

    private TextField addAnotherActionTextField(GridPane fieldGrid){
        TextField field = new TextField();
        field.setMaxWidth(100);
        fieldGrid.add(field, fieldGrid.getChildren().size(), 0);

        controller.actionList.get((int)fieldGrid.getUserData()).add(field);

        return field;
    }

    private void removeActionTextField(GridPane fieldGrid){
        ObservableList<Node> list = fieldGrid.getChildren();
        if(list.size() > 0) {
            controller.actionList.get((int)fieldGrid.getUserData()).remove(list.size()-1);
            list.remove(list.size() - 1);
        }
    }

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
