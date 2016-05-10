import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;

/**
 * Created by Paha on 4/2/2016.
 */
public class EventMode {
    Stage primaryStage;

    int descNumRow = 0;
    int choiceRow = 0;
    int choiceCol = 0;
    int choiceCounter = 0;
    int actionAreaCounter = 0;
    int restrictionAreaCounter = 0;

    GridPane mainGrid = new GridPane();
    GridPane descGrid = new GridPane();

//    GridPane restrictionGrid = new GridPane();
//    GridPane restrictionsListGrid = new GridPane();

    GridPane choiceGrid = new GridPane();
    GridPane choiceListGrid = new GridPane();

    GridPane actionAreaGrid = new GridPane();

    GridPane topGrid = new GridPane();
    GridPane bottomGrid = new GridPane();

    EventController eventController = new EventController();

    public void resetGrids(){
        descGrid = new GridPane();

        choiceGrid = new GridPane();
        choiceListGrid = new GridPane();

        actionAreaGrid = new GridPane();
//        restrictionGrid = new GridPane();
//        restrictionsListGrid = new GridPane();

        descNumRow = 0;
        choiceRow = 0;
        choiceCol = 0;
        choiceCounter = 0;
        actionAreaCounter=0;
    }

    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        eventController.loadAllJsonFiles("");

        primaryStage.setTitle("GUI");

        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setPadding(new Insets(25, 25, 25, 25));

        ScrollPane sp = new ScrollPane();
        sp.setContent(mainGrid);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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
        Button replaceButton = new Button("Replace");
        Button loadButton = new Button("Load");

        GridPane loadFileGrid = new GridPane();
        ObservableList<String> options = FXCollections.observableArrayList(Helper.asSortedList(eventController.fileMap.keySet()));
        eventController.fileComboBox = new ComboBox<>(options);
        Button loadFileButton = new Button("Load File");

        loadFileGrid.add(eventController.fileComboBox, 0, 0);
        loadFileGrid.add(loadFileButton, 1, 0);

        options = FXCollections.observableArrayList(Helper.asSortedList(eventController.eventMap.keySet()));
        eventController.eventComboBox = new ComboBox<>(options);

        buttonGrid.add(saveButton, 1, 0);
        buttonGrid.add(newButton, 2, 0);
        buttonGrid.add(removeButton, 3, 0);
        buttonGrid.add(replaceButton, 4, 0);
        buttonGrid.add(loadButton, 5, 0);
        buttonGrid.add(eventController.eventComboBox, 6, 0);
        buttonGrid.add(loadFileGrid, 0, 1, 10, 1);

        topGrid.add(buttonGrid, 0, 2, 99, 1);

        saveButton.setOnAction((ActionEvent e) -> {
            eventController.save();
            eventController.writeToJson();
            eventController.loadEventList(eventController.fileMap.get(eventController.fileComboBox.getValue()));
        });

        loadButton.setOnAction((ActionEvent e) -> loadEvent(eventController.eventComboBox.getValue()));
        newButton.setOnAction((ActionEvent e) -> reset());
        loadFileButton.setOnAction((ActionEvent e) -> eventController.loadEventList(eventController.fileMap.get(eventController.fileComboBox.getValue())));

        switchButton.setOnAction(e -> {clean();
            try {
                new SearchActivityMode().start(primaryStage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        replaceButton.setOnAction(e -> {
            if(!eventController.nameTextField.getText().isEmpty())
                createReplacementAlert();
        });

        removeButton.setOnAction(e -> {
            if(!eventController.nameTextField.getText().isEmpty())
                createDeletionAlert();
        });
    }

    public void createDeletionAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("About to delete "+ eventController.nameTextField.getText()+".");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            eventController.eventMap.remove(eventController.nameTextField.getText());
            reset();
            eventController.writeToJson();
            eventController.loadEventList(eventController.fileMap.get(eventController.fileComboBox.getValue()));
        } else {
            alert.close();
        }
    }

    public void createReplacementAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("About to replace "+ eventController.eventComboBox.getValue()+" with "+ eventController.nameTextField.getText());
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            eventController.replace();
        } else {
            alert.close();
        }
    }

    public void addBottomGrid(){
        Label root = new Label("Root:");

        eventController.isRoot = new CheckBox();

        Label title = new Label("Title:");

        eventController.titleTextField = new TextField();
        eventController.titleTextField.setMaxWidth(200);

        Label nameLabel = new Label("Name:");

        eventController.nameTextField = new TextField();
        eventController.nameTextField.setMaxWidth(200);

        bottomGrid.add(root, 0, 3);
        bottomGrid.add(eventController.isRoot, 1, 3);
        bottomGrid.add(title, 0, 4);
        bottomGrid.add(eventController.titleTextField, 1, 4);
        bottomGrid.add(nameLabel, 0, 5);
        bottomGrid.add(eventController.nameTextField, 1, 5);
        bottomGrid.add(descGrid, 0, 6, 3, 1);
        bottomGrid.add(choiceGrid, 0, 7, 3, 1);
        bottomGrid.add(actionAreaGrid, 0, 8, 99, 1);
//        bottomGrid.add(restrictionGrid, 0, 9, 99, 1);

        descriptionArea();
        choicesArea();
        actionArea();
//        setupRestrictionArea();
    }

    void clean(){
        mainGrid.getChildren().clear();
    }

    void reset(){
        bottomGrid.getChildren().clear();
        eventController.reset();
        resetGrids();
        addBottomGrid();
    }

    void loadEvent(String name){
        reset();
        EventController.JsonEvent event = eventController.eventMap.get(name);

        eventController.isRoot.setSelected(event.root);
        eventController.titleTextField.setText(event.title);
        eventController.nameTextField.setText(event.name);

        for(int i=0;i<event.description.length;i++){
            if(i==0){
                eventController.descList.get(i).setText(event.description[i]);
            }else{
                addDescriptionArea();
                eventController.descList.get(i).setText(event.description[i]);
            }
        }

        //Since we can have an outcome(s) without a choice, choose the correct loop to limit.
        int size = Integer.max(event.choices.length, event.outcomes.size());

        for(int i=0;i<size;i++){
            GridPane[] grids = addNewChoiceArea();
            ChoiceLink link = eventController.choiceList.get(i);
            //The choice may not exist so check here.
            if(event.choices.length > i)
                link.choiceText.setText(event.choices[i]);

            if(event.outcomes.size() >= i) {
                for (int j = 0; j < event.outcomes.get(i).size(); j++) {
                    //This is because when the choice area is initially created, the first text fields are already created.
                    if(j != 0) {
                        addOutcomeTextBox(grids[1]);
                        addAnotherChance(grids[2]);
                        addAnotherRestriction(grids[3]);
                    }

                    link.outcomeList.get(j).setText(event.outcomes.get(i).get(j));
                    if(event.chances.size() > 0)
                        link.chanceList.get(j).setText(event.chances.get(i).get(j).toString());

                    if(event.restrictions.size() > j)
                        link.restrictionList.get(j).setText(event.restrictions.get(i).get(j));
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
        eventController.descList.add(descBox);
        descBox.setMaxWidth(200);
        descBox.setWrapText(true);
        descGrid.add(descBox, 1, descNumRow);

        return descBox;
    }

    void removeDescriptionArea(){
        ObservableList<Node> list = descGrid.getChildren();
        list.remove(list.size()-1);
        eventController.descList.remove(eventController.descList.size()-1);

        descNumRow--;
    }

    void choicesArea(){
        GridPane labelButtonGrid = new GridPane();
//        choiceListGrid.setGridLinesVisible(true);

        Label desc = new Label("Choices:");
        labelButtonGrid.add(desc, 0, 0);

        Button lessButton = new Button("-");
        labelButtonGrid.add(lessButton, 2, 0);

        Button addMoreButton = new Button("+");
        labelButtonGrid.add(addMoreButton, 3, 0);

        addMoreButton.setOnAction((ActionEvent e) -> addNewChoiceArea());

        lessButton.setOnAction((ActionEvent e) -> {
            removeChoiceArea();
        });

        choiceGrid.add(labelButtonGrid, 0, 0);
        choiceGrid.add(choiceListGrid, 0, 1);
    }

    /**
     * Adds a new choice area that will hold the choice, outcomes, and chances.
     * @return An array of gridpanes. [1] is the gridPane for choices, [2] is the outcome gridpane, [3] is the chances gridpane.
     */
    GridPane[] addNewChoiceArea(){
        TextField choiceBox = new TextField();
        choiceBox.setMaxWidth(100);

        eventController.choiceList.add(new ChoiceLink());

        GridPane choiceBoxGrid = addChoiceBox();
        GridPane[] outcomeGrid = createOutcomeArea();

        outcomeGrid[2].setUserData(choiceCounter++); //Set the user data of the chance grid

        addAnotherChance(outcomeGrid[2]); //Adds the first chance
        addOutcomeTextBox(outcomeGrid[1]); //Adds the first outcome
        addAnotherRestriction(outcomeGrid[3]); //Adds the first restriction

        choiceListGrid.add(choiceBoxGrid, choiceCol, choiceRow++); //Add the choice text box
        choiceListGrid.add(outcomeGrid[0], 0, choiceRow++); //Add the outcome area
        choiceListGrid.add(outcomeGrid[2], 0, choiceRow++); //Add the chances area.
        choiceListGrid.add(outcomeGrid[3], 0, choiceRow++); //Add the restriction area.
        choiceCol=0;

        return new GridPane[]{choiceBoxGrid, outcomeGrid[1], outcomeGrid[2], outcomeGrid[3]};
    }

    void removeChoiceArea(){
        ObservableList<Node> list = choiceListGrid.getChildren();
        list.remove(list.size() - 1);
        list.remove(list.size() - 1);
        list.remove(list.size() - 1);

        choiceCounter--;

        eventController.choiceList.remove(eventController.choiceList.size()-1);
        choiceRow-=3;
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

        eventController.choiceList.get(eventController.choiceList.size()-1).choiceText = choiceTextField;

        grid.add(choiceLabel, 0, 0);
        grid.add(choiceTextField, 0, 1);

        return grid;
    }

    /**
     *
     * @return A GridPane array of the main outcome grid, outcome text field grid (for input), chances grid, restriction grid
     */
    GridPane[] createOutcomeArea(){
        GridPane outcomeGrid = new GridPane();
        outcomeGrid.setPadding(new Insets(0, 0, 0, 0));

        GridPane labelButtonGrid = new GridPane();
        GridPane outcomeTextBoxGrid = new GridPane();
        GridPane chancesGrid = createChancesArea();
        GridPane restrictionGrid = createRestrictionArea();

        outcomeTextBoxGrid.setUserData(choiceCounter);

        Label desc = new Label("Outcomes:");
        labelButtonGrid.add(desc, 0, 0);

        Button lessButton = new Button("-");
        labelButtonGrid.add(lessButton, 2, 0);

        Button addMoreButton = new Button("+");
        labelButtonGrid.add(addMoreButton, 3, 0);

        //Add another outcome box and another chance box.
        addMoreButton.setOnAction((ActionEvent e) -> {
            addOutcomeTextBox(outcomeTextBoxGrid);
            addAnotherChance(chancesGrid);
            addAnotherRestriction(restrictionGrid);
        });

        //Remove an outcome box and chance box.
        lessButton.setOnAction((ActionEvent e) -> {
            removeOutcomeTextBox(outcomeTextBoxGrid);
            removeAnotherChance(chancesGrid);
            removeRestrictionArea(restrictionGrid);
        });

        outcomeGrid.add(labelButtonGrid, 0, 0);
        outcomeGrid.add(outcomeTextBoxGrid, 0, 1);

        GridPane.setColumnSpan(outcomeGrid, 20);

        return new GridPane[]{outcomeGrid, outcomeTextBoxGrid, chancesGrid, restrictionGrid};
    }

    /**
     * Adds another outcome text box.
     * @param outcomeTextBoxGrid
     */
    TextField addOutcomeTextBox(GridPane outcomeTextBoxGrid){
        TextField outcomeTextBox = new TextField();
        outcomeTextBox.setMaxWidth(100);
        outcomeTextBoxGrid.add(outcomeTextBox, outcomeTextBoxGrid.getChildrenUnmodifiable().size(), 0);

        eventController.choiceList.get((int)outcomeTextBoxGrid.getUserData()).outcomeList.add(outcomeTextBox);

        return outcomeTextBox;
    }

    void removeOutcomeTextBox(GridPane outcomeTextBoxGrid){
        ObservableList<Node> list = outcomeTextBoxGrid.getChildren();
        if(list.size() > 1) {
            eventController.choiceList.get((int)outcomeTextBoxGrid.getUserData()).outcomeList.remove(list.size()-1);
            list.remove(list.size() - 1);
        }
    }

    GridPane createChancesArea(){
        GridPane chanceGrid = new GridPane();

        chanceGrid.setPadding(new Insets(0, 0, 0, 0));

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

        eventController.choiceList.get(eventController.choiceList.size()-1).chanceList.add(chanceBox);
    }

    /**
     * Removes a textfield from a grid.
     * @param chanceGrid The GridPane to remove from.
     */
    void removeAnotherChance(GridPane chanceGrid){
        ObservableList<Node> list = chanceGrid.getChildren();
        if(list.size() > 2) {
            list.remove(list.size() - 1);
        }
    }

    GridPane createRestrictionArea(){
        GridPane restrictionGrid = new GridPane();

        restrictionGrid.setPadding(new Insets(0, 0, 20, 0));

        Label desc = new Label("Restrictions:");
        restrictionGrid.add(desc, 0, 0);

        GridPane.setColumnSpan(restrictionGrid, 20);
        return restrictionGrid;
    }

    void addAnotherRestriction(GridPane restrictionTextBoxGrid){
        TextField restrictionBox = new TextField();
        restrictionBox.setMaxWidth(100);
        restrictionTextBoxGrid.add(restrictionBox, restrictionTextBoxGrid.getChildren().size()-1, 1);

        eventController.choiceList.get(eventController.choiceList.size()-1).restrictionList.add(restrictionBox);
    }

    void removeRestrictionArea(GridPane restrictionGrid){
        ObservableList<Node> list = restrictionGrid.getChildren();
        if(list.size() > 2) {
            list.remove(list.size() - 1);
        }
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
        eventController.actionList.add(new ArrayList<>());

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
            eventController.actionList.remove(eventController.actionList.size()-1);
            actionAreaCounter--;
        }
    }

    private TextField addAnotherActionTextField(GridPane fieldGrid){
        TextField field = new TextField();
        field.setMaxWidth(100);
        fieldGrid.add(field, fieldGrid.getChildren().size(), 0);

        eventController.actionList.get((int)fieldGrid.getUserData()).add(field);

        return field;
    }

    private void removeActionTextField(GridPane fieldGrid){
        ObservableList<Node> list = fieldGrid.getChildren();
        if(list.size() > 0) {
            eventController.actionList.get((int)fieldGrid.getUserData()).remove(list.size()-1);
            list.remove(list.size() - 1);
        }
    }

//    public GridPane[] setupRestrictionArea(){
//        GridPane labelGrid = new GridPane();
//
//        Label label = new Label("Restrictions");
//        Button lessButton = new Button("-");
//        Button moreButton = new Button("+");
//
//        labelGrid.add(label, 0, 0);
//        labelGrid.add(lessButton, 1, 0);
//        labelGrid.add(moreButton, 2, 0);
//
//        moreButton.setOnAction(e -> addAnotherRestrictionArea());
//        lessButton.setOnAction(e -> removeRestrictionArea());
//
//        restrictionGrid.add(labelGrid, 0, 0);
//        restrictionGrid.add(restrictionsListGrid, 0, 1);
//
//        return new GridPane[]{labelGrid, restrictionsListGrid};
//    }
//
//    /**
//     * Adds another restriction area.
//     * @return A GridPane array containing the restriction (main) restriction area grid, label grid, and parameter grid.
//     */
//    public GridPane[] addAnotherRestrictionArea(){
//        eventController.restrictionList.add(new ArrayList<>());
//
//        GridPane restrictionAreaGrid = new GridPane(); //Holds both the labels and params grids
//        GridPane labelGrid = new GridPane(); //Holds the 'restrictions' label label and +,- buttons
//        GridPane restrictionParamsGrid = new GridPane(); //Holds all the individual restriction fields
//
//        restrictionParamsGrid.setUserData(restrictionAreaCounter); //Sets the counter as the data
//
//        Label label = new Label("Restriction:"); //The title
//
//        //The -,+ buttons for less, more.
//        Button lessButton = new Button("-");
//        Button moreButton = new Button("+");
//
//        //Add them to the label grid.
//        labelGrid.add(label, 0, 0);
//        labelGrid.add(lessButton, 1, 0);
//        labelGrid.add(moreButton, 2, 0);
//
//        //Button actions for more/less
//        moreButton.setOnAction(e -> addRestrictionField(restrictionParamsGrid));
//        lessButton.setOnAction(e -> removeRestrictionField(restrictionParamsGrid));
//
//        //Add the label grid and restrictions grid to the sub params grid.
//        restrictionAreaGrid.add(labelGrid, 0, 0);
//        restrictionAreaGrid.add(restrictionParamsGrid, 0, 1);
//
//        //Add the params grid to the main restriction grid.
//        restrictionsListGrid.add(restrictionAreaGrid, 0, ++restrictionAreaCounter, 99, 1);
//
//        return new GridPane[]{restrictionAreaGrid, labelGrid, restrictionParamsGrid};
//    }
//
//    private void removeRestrictionArea(){
//        ObservableList<Node> list = restrictionsListGrid.getChildren();
//        if(list.size() > 0) {
//            list.remove(list.size() - 1);
//            eventController.restrictionList.remove(eventController.restrictionList.size()-1);
//            restrictionAreaCounter--;
//        }
//    }
//
//    /**
//     * Removes the last added restriction field from the params grid passed in.
//     * @param restrictionParamsGrid The parameter grid to remove from.
//     */
//    public void removeRestrictionField(GridPane restrictionParamsGrid){
//        ObservableList<Node> list = restrictionParamsGrid.getChildren();
//        if(list.size() > 0){
//            list.remove(list.size()-1);
//            int pos = (int)(restrictionParamsGrid.getUserData());
//            eventController.restrictionList.get(pos).remove(eventController.restrictionList.get(pos).size()-1);
//        }
//    }
//
//    /**
//     * Adds another restriction field to the restriction paramater grid.
//     * @param restrictionParamsGrid The GridPane to add to.
//     * @return The TextField that was added.
//     */
//    public TextField addRestrictionField(GridPane restrictionParamsGrid){
//        TextField field = new TextField();
//        field.setMaxWidth(100f);
//
//        restrictionParamsGrid.add(field, restrictionParamsGrid.getChildrenUnmodifiable().size(), 0);
//        int pos = (int)(restrictionParamsGrid.getUserData());
//        eventController.restrictionList.get(pos).add(field);
//
//        return field;
//    }
}
