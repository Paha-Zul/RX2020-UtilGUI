import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import java.util.Collection;
import java.util.List;

/**
 * Created by Paha on 4/2/2016.
 */
public class EventMode {
    Stage primaryStage;

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

    GridPane topGrid = new GridPane();
    GridPane bottomGrid = new GridPane();

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

    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        controller.loadAllJsonFiles("");

        primaryStage.setTitle("GUI");

        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setPadding(new Insets(25, 25, 25, 25));

        ScrollPane sp = new ScrollPane();
        sp.setContent(mainGrid);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

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

        Text scenetitle = new Text("Events");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        topGrid.add(scenetitle, 0, 0, 2, 1);
        topGrid.add(switchButton, 0, 1);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setVgap(20);
        Button saveButton = new Button("Save");
        Button newButton = new Button("Clear");
        Button removeButton = new Button("Remove");
        Button loadButton = new Button("Load");

        GridPane loadFileGrid = new GridPane();
        ObservableList<String> options = FXCollections.observableArrayList(asSortedList(controller.fileMap.keySet()));
        ComboBox<String> fileDropList = new ComboBox<>(options);
        Button loadFileButton = new Button("Load File");

        loadFileGrid.add(fileDropList, 0, 0);
        loadFileGrid.add(loadFileButton, 1, 0);

        options = FXCollections.observableArrayList(asSortedList(controller.eventMap.keySet()));
        controller.eventComboBox = new ComboBox<>(options);

        buttonGrid.add(saveButton, 1, 0);
        buttonGrid.add(newButton, 2, 0);
        buttonGrid.add(removeButton, 3, 0);
        buttonGrid.add(loadButton, 4, 0);
        buttonGrid.add(controller.eventComboBox, 5, 0);
        buttonGrid.add(loadFileGrid, 0, 1, 10, 1);

        topGrid.add(buttonGrid, 0, 2, 99, 1);

        saveButton.setOnAction((ActionEvent e) -> {
            controller.save();
            controller.writeToJson("complicated.json");
            controller.loadEventList("complicated.json");
            controller.eventComboBox.setItems(FXCollections.observableArrayList(asSortedList(controller.eventMap.keySet())));
        });

        loadButton.setOnAction((ActionEvent e) -> loadEvent(controller.eventComboBox.getValue()));
        newButton.setOnAction((ActionEvent e) -> reset());
        loadFileButton.setOnAction((ActionEvent e) -> controller.loadEventList(controller.fileMap.get(fileDropList.getValue())));

        switchButton.setOnAction(e -> {clean();
            try {
                new SearchActivityMode().start(primaryStage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        removeButton.setDisable(true);
    }

    public void addBottomGrid(){
        Label root = new Label("Root:");

        controller.isRoot = new CheckBox();

        Label title = new Label("Title:");

        controller.title = new TextField();
        controller.title.setMaxWidth(200);

        Label nameLabel = new Label("Name:");

        controller.name = new TextField();
        controller.name.setMaxWidth(200);

        bottomGrid.add(root, 0, 3);
        bottomGrid.add(controller.isRoot, 1, 3);
        bottomGrid.add(title, 0, 4);
        bottomGrid.add(controller.title, 1, 4);
        bottomGrid.add(nameLabel, 0, 5);
        bottomGrid.add(controller.name, 1, 5);
        bottomGrid.add(descGrid, 0, 6, 3, 1);
        bottomGrid.add(choiceGrid, 0, 7, 3, 1);
        bottomGrid.add(actionAreaGrid, 0, 8, 99, 1);

        descriptionArea();
        choicesArea();
        actionArea();
    }

    void clean(){
        mainGrid.getChildren().clear();
    }

    void reset(){
        bottomGrid.getChildren().clear();
        controller.reset();
        resetGrids();
        addBottomGrid();
    }

    void loadEvent(String name){
        reset();
        Controller.JsonEvent event = controller.eventMap.get(name);

        controller.isRoot.setSelected(event.root);
        controller.title.setText(event.title);
        controller.name.setText(event.name);

        for(int i=0;i<event.description.length;i++){
            if(i==0){
                controller.descList.get(i).setText(event.description[i]);
            }else{
                addDescriptionArea();
                controller.descList.get(i).setText(event.description[i]);
            }
        }

        for(int i=0;i<event.choices.length;i++){
            GridPane[] grids = addNewChoiceArea();
            ChoiceLink link = controller.choiceList.get(i);
            link.choiceText.setText(event.choices[i]);

            if(event.outcomes.size() >= i) {
                for (int j = 0; j < event.outcomes.get(i).size(); j++) {
                    addOutcomeTextBox(grids[1]);
                    addAnotherChance(grids[2]);

                    link.outcomeList.get(j).setText(event.outcomes.get(i).get(j));
                    link.chanceList.get(j).setText(event.chances.get(i).get(j).toString());
                }
            }
        }

        ArrayList<ArrayList<String>> actionList = event.resultingAction;
        if(actionList != null) {
            for (ArrayList<String> list : actionList) {
                GridPane[] grids = addAnotherAction();
                for(String param : list){
                    addAnotherActionTextField(grids[2]).setText(param);
                }
            }
        }
    }

    void descriptionArea(){
        GridPane buttonGrid = new GridPane();
        descGrid.add(buttonGrid, 0, 0, 99, 1);

        //Simple label
        Label desc = new Label("Description:");
        buttonGrid.add(desc, 0, 0);

        Button addMoreButton = new Button("+");
        buttonGrid.add(addMoreButton, 2, 0);

        Button removeButton = new Button("-");
        buttonGrid.add(removeButton, 1, 0);

        //Where the description is entered.
        addDescriptionArea();

        addMoreButton.setOnAction((ActionEvent e) -> addDescriptionArea());
        removeButton.setOnAction((ActionEvent e) -> removeDescriptionArea());
    }

    TextArea addDescriptionArea(){
        descNumRow++;

        TextArea descBox = new TextArea();
        controller.descList.add(descBox);
        descBox.setMaxWidth(200);
        descBox.setWrapText(true);
        descGrid.add(descBox, 1, descNumRow);

        return descBox;
    }

    void removeDescriptionArea(){
        ObservableList<Node> list = descGrid.getChildren();
        list.remove(list.size()-1);

        descNumRow--;
    }

    void choicesArea(){
        GridPane labelButtonGrid = new GridPane();
//        choiceTextGrid.setGridLinesVisible(true);

        Label desc = new Label("Choices:");
        labelButtonGrid.add(desc, 0, 0);

        Button lessButton = new Button("-");
        labelButtonGrid.add(lessButton, 2, 0);

        Button addMoreButton = new Button("+");
        labelButtonGrid.add(addMoreButton, 3, 0);

        addMoreButton.setOnAction((ActionEvent e) -> addNewChoiceArea());

        lessButton.setOnAction((ActionEvent e) -> {
            ObservableList<Node> list = choiceTextGrid.getChildren();
            list.remove(list.size() - 1);
            list.remove(list.size() - 1);
            list.remove(list.size() - 1);

            controller.choiceList.remove(controller.choiceList.size()-1);

            choiceRow-=3;
        });

        choiceGrid.add(labelButtonGrid, 0, 0);
        choiceGrid.add(choiceTextGrid, 0, 1);
    }

    /**
     * Adds a new choice area that will hold the choice, outcomes, and chances.
     * @return An array of gridpanes. [1] is the gridPane for choices, [2] is the outcome gridpane, [3] is the chances gridpane.
     */
    GridPane[] addNewChoiceArea(){
        TextField choiceBox = new TextField();
        choiceBox.setMaxWidth(100);

        controller.choiceList.add(new ChoiceLink());

        GridPane choiceBoxGrid = addChoiceBox();

        GridPane chancesGrid = createChancesArea();
        addAnotherChance(chancesGrid);

        GridPane[] outcomeGrid = createOutcomeArea(chancesGrid);

        choiceTextGrid.add(choiceBoxGrid, choiceCol, choiceRow++); //Add the choice text box
        choiceTextGrid.add(outcomeGrid[0], 0, choiceRow++); //Add the outcome area
        choiceTextGrid.add(chancesGrid, 0, choiceRow++); //Add the chances area.
        choiceCol=0;

        return new GridPane[]{choiceBoxGrid, outcomeGrid[1], chancesGrid};
    }

    /**
     * Adds a new choice box with a label and a textfield.
     * @return The GridPane that holds the label and textfield.
     */
    GridPane addChoiceBox(){
        GridPane grid = new GridPane();

        Label choiceLabel = new Label("Choice:");

        TextField choiceTextField = new TextField();
        choiceTextField.setMaxWidth(100);

        controller.choiceList.get(controller.choiceList.size()-1).choiceText = choiceTextField;

        grid.add(choiceLabel, 0, 0);
        grid.add(choiceTextField, 0, 1);

        return grid;
    }

    GridPane[] createOutcomeArea(GridPane chancesGrid){
        GridPane outcomeGrid = new GridPane();
        outcomeGrid.setPadding(new Insets(0, 0, 0, 0));

        GridPane labelButtonGrid = new GridPane();
        GridPane outcomeTextBoxGrid = new GridPane();

        Label desc = new Label("Outcomes:");
        labelButtonGrid.add(desc, 0, 0);

        addOutcomeTextBox(outcomeTextBoxGrid);

        Button lessButton = new Button("-");
        labelButtonGrid.add(lessButton, 2, 0);

        Button addMoreButton = new Button("+");
        labelButtonGrid.add(addMoreButton, 3, 0);

        //Add another outcome box and another chance box.
        addMoreButton.setOnAction((ActionEvent e) -> {

            addOutcomeTextBox(outcomeTextBoxGrid);

            addAnotherChance(chancesGrid);
        });

        //Remove an outcome box and chance box.
        lessButton.setOnAction((ActionEvent e) -> {
            ObservableList<Node> list = outcomeTextBoxGrid.getChildren();
            if(list.size() > 1) {
                list.remove(list.size() - 1);
                removeAnotherChance(chancesGrid);
            }
        });

        outcomeGrid.add(labelButtonGrid, 0, 0);
        outcomeGrid.add(outcomeTextBoxGrid, 0, 1);

        GridPane.setColumnSpan(outcomeGrid, 20);

        return new GridPane[]{outcomeGrid, outcomeTextBoxGrid};
    }

    /**
     * Adds another outcome text box.
     * @param outcomeTextBoxGrid
     */
    void addOutcomeTextBox(GridPane outcomeTextBoxGrid){
        outcomeCounter++;

        TextField outcomeTextBox = new TextField();
        outcomeTextBox.setMaxWidth(100);
        outcomeTextBoxGrid.add(outcomeTextBox, outcomeCounter, 0);

        controller.choiceList.get(controller.choiceList.size()-1).outcomeList.add(outcomeTextBox);
    }

    GridPane createChancesArea(){
        GridPane chanceGrid = new GridPane();

        chanceGrid.setPadding(new Insets(0, 0, 20, 0));

        Label desc = new Label("Chances:");
        chanceGrid.add(desc, 0, 0);

        GridPane.setColumnSpan(chanceGrid, 20);
        return chanceGrid;
    }

    /**
     * Adds another textfield to a grid.
     * @param chanceTextBoxGrid The GridPane to add to.
     */
    void addAnotherChance(GridPane chanceTextBoxGrid){
        TextField chanceBox = new TextField();
        chanceBox.setMaxWidth(100);
        chanceTextBoxGrid.add(chanceBox, chanceTextBoxGrid.getChildren().size()-1, 1);

        controller.choiceList.get(controller.choiceList.size()-1).chanceList.add(chanceBox);
    }

    /**
     * Removes a textfield from a grid.
     * @param grid The GridPane to remove from.
     */
    void removeAnotherChance(GridPane grid){
        ObservableList<Node> list = grid.getChildren();
        list.remove(list.size()-1);
    }

    void actionArea(){
        Label actionsLabel = new Label("Actions");
        Button lessButton = new Button("-");
        Button moreButton = new Button("+");

        actionAreaGrid.add(actionsLabel, 0, 0);
        actionAreaGrid.add(lessButton, 1, 0);
        actionAreaGrid.add(moreButton, 2, 0);

        moreButton.setOnAction(e -> addAnotherAction());
        lessButton.setOnAction(e -> removeAction());
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
