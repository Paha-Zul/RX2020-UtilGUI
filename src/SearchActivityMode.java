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

import java.util.ArrayList;

/**
 * Created by Paha on 4/2/2016.
 */
public class SearchActivityMode {
    Stage primaryStage;

    GridPane mainGrid = new GridPane();
    GridPane actionAreaGrid = new GridPane();
    GridPane topGrid = new GridPane();
    GridPane bottomGrid = new GridPane();

    GridPane restrictionGrid = new GridPane();
    GridPane restrictionFieldGrid = new GridPane();

    int actionAreaCounter = 0;

    ActivityController controller = new ActivityController();

    public void start(Stage primaryStage) throws Exception{
        controller.loadAllJsonFiles("");
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

        mainGrid.add(topGrid, 0, 0);
        mainGrid.add(bottomGrid, 0, 1);
        addTopGrid();
        addBottomGrid();
    }

    public void addTopGrid(){
        Button switchButton = new Button("Switch");

        Text scenetitle = new Text("Search Activities");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        topGrid.add(scenetitle, 0, 0, 2, 1);
        topGrid.add(switchButton, 0, 1);

        GridPane buttonGrid = new GridPane();

        Button saveButton = new Button("Save");
        Button clearButton = new Button("Clear");
        Button loadButton = new Button("Load");

        GridPane loadFileGrid = new GridPane();
        Label statusLabel = new Label("");
        ObservableList<String> options = FXCollections.observableArrayList(Helper.asSortedList(controller.fileMap.keySet()));
        ComboBox<String> fileDropList = new ComboBox<>(options);
        Button loadFileButton = new Button("Load File");

        loadFileGrid.add(fileDropList, 0,0);
        loadFileGrid.add(loadFileButton, 1, 0);
        loadFileGrid.add(statusLabel, 2, 0);

        ObservableList<String> list = FXCollections.observableArrayList(Helper.asSortedList(controller.activityMap.keySet()));
        controller.activityComboBox = new ComboBox<>(list);

        buttonGrid.add(saveButton, 0, 0);
        buttonGrid.add(clearButton, 1, 0);
        buttonGrid.add(loadButton, 2, 0);
        buttonGrid.setVgap(20);
        buttonGrid.add(controller.activityComboBox, 3, 0);
        buttonGrid.add(loadFileGrid, 0, 1, 10, 1);

        topGrid.add(buttonGrid, 0, 2);

        saveButton.setOnAction(ae -> controller.save());
        clearButton.setOnAction(ae -> reset());
        loadButton.setOnAction(ae -> loadActivityIntoGUI());

        loadFileButton.setOnAction(e -> {
            boolean status = true;
            status = controller.loadSearchActivities(controller.fileMap.get(fileDropList.getValue()));
            if(status) {
                controller.activityComboBox.setItems(FXCollections.observableArrayList(Helper.asSortedList(controller.activityMap.keySet())));
                statusLabel.setText("Loaded!");
            }else
                statusLabel.setText("Wrong json file");

        });

        switchButton.setOnAction(e -> {clean();
            try {
                new EventMode().start(primaryStage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    public void addBottomGrid(){
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

        bottomGrid.add(nameLabel, 0, 3);
        bottomGrid.add(controller.nameField, 0, 4);
        bottomGrid.add(buttonTitleLabel, 0, 5);
        bottomGrid.add(controller.buttonTitleField, 0, 6);
        bottomGrid.add(descLabel, 0, 7);
        bottomGrid.add(controller.descTextArea, 0, 8);
        bottomGrid.add(actionAreaGrid, 0, 9);
        bottomGrid.add(restrictionGrid, 0, 10);

        setupRestrictionArea();

        moreButton.setOnAction(e -> addAnotherAction());
        lessButton.setOnAction(e -> removeAction());
    }

    public void clean(){
        mainGrid.getChildren().clear();
    }

    void reset(){
        bottomGrid.getChildren().clear();
        actionAreaGrid = new GridPane();
        restrictionGrid = new GridPane();
        restrictionFieldGrid = new GridPane();
        actionAreaCounter = 0;
        addBottomGrid();
    }

    public void loadActivityIntoGUI(){
        //We need to get the activity before we clear the GUI.
        ActivityController.SearchActivityJSON act = controller.activityMap.get(controller.activityComboBox.getValue());

        controller.resetControllerLists();
        reset();

        controller.nameField.setText(act.name);
        controller.buttonTitleField.setText(act.buttonTitle);
        controller.descTextArea.setText(act.description);

        for (ArrayList<String> list : act.action) {
            GridPane[] grids = addAnotherAction();
            for (String param : list) {
                addAnotherActionTextField(grids[2]).setText(param);
            }
        }

        if(act.restrictions != null) {
            for (String res : act.restrictions) {
                addRestrictionField().setText(res);
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

    public GridPane[] setupRestrictionArea(){
        GridPane labelGrid = new GridPane();
        restrictionFieldGrid = new GridPane();

        Label label = new Label("Restrictions");
        Button lessButton = new Button("-");
        Button moreButton = new Button("+");

        labelGrid.add(label, 0, 0);
        labelGrid.add(lessButton, 1, 0);
        labelGrid.add(moreButton, 2, 0);

        moreButton.setOnAction(e -> addRestrictionField());
        lessButton.setOnAction(e -> removeRestrictionField());

        restrictionGrid.add(labelGrid, 0, 0);
        restrictionGrid.add(restrictionFieldGrid, 0, 1);

        return new GridPane[]{labelGrid, restrictionFieldGrid};
    }

    public TextField addRestrictionField(){
        TextField field = new TextField();
        field.setMaxWidth(100f);

        restrictionFieldGrid.add(field, restrictionFieldGrid.getChildrenUnmodifiable().size(), 0);
        controller.restrictionList.add(field);

        return field;
    }

    public void removeRestrictionField(){
        ObservableList<Node> list = restrictionFieldGrid.getChildren();
        if(list.size() > 0){
            list.remove(list.size()-1);
            controller.restrictionList.remove(controller.restrictionList.size()-1);
        }
    }

    private void removeActionTextField(GridPane fieldGrid){
        ObservableList<Node> list = fieldGrid.getChildren();
        if(list.size() > 0) {
            controller.actionList.get((int)fieldGrid.getUserData()).remove(list.size()-1);
            list.remove(list.size() - 1);
        }
    }
}
