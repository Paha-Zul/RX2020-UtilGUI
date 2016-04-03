import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Controller {

    HashMap<String, JsonEvent> eventMap = new HashMap<>();
    HashMap<String, String> fileMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();
    private JsonEvent event;

    public ComboBox<String> eventComboBox;

    public CheckBox isRoot;
    public TextField title, name;

    public ArrayList<TextArea> descList = new ArrayList<>();
    public ArrayList<ChoiceLink> choiceList = new ArrayList<>();
    public ArrayList<ArrayList<TextField>> actionList = new ArrayList<>();

    public void reset(){
        descList = new ArrayList<>();
        choiceList = new ArrayList<>();
    }

    public void loadAllJsonFiles(String path){
        File currPath = new File(new File(path).getAbsolutePath());

        File[] list = currPath.listFiles();
        if(list != null) {
            for (File file : list) {
                if (file.isDirectory()) {
                    loadAllJsonFiles(file.getAbsolutePath());
                } else {
                    if (file.getPath().endsWith(".json"))
                        fileMap.put(file.getName(), file.getAbsolutePath());
                }
            }
        }
    }

    public void loadEventList(String fileName) {
        File eventFile = new File(fileName);
        if (!eventFile.isDirectory() && eventFile.exists()){
            try {
                JsonEvent[] events = mapper.readValue(eventFile, JsonEvent[].class);
                for (JsonEvent evt : events)
                    eventMap.put(evt.name, evt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        eventComboBox.setItems(FXCollections.observableArrayList(eventMap.keySet()));
    }

    /**
     *
     */
    public void save(){
        event = new JsonEvent();
        event.root = isRoot.isSelected();
        event.title = title.getText();
        event.name = name.getText();

        /*
         * Record all the descriptions. Only use child 3+
         */
        ArrayList<TextArea> descList = this.descList;
        event.description = new String[descList.size()];
        for(int i=0;i<descList.size();i++) //Record all the text.
            event.description[i] = descList.get(i).getText();


        /*
         * Record all the choices. Complicated much very.
         */
        int i=0;
        ArrayList<ChoiceLink> choiceList = this.choiceList;
        event.choices = new String[choiceList.size()];
        for(ChoiceLink link : choiceList){
            ArrayList<String> outcomes = new ArrayList<>();
            ArrayList<Integer> chances = new ArrayList<>();
            ArrayList<String> actions = new ArrayList<>();

            event.choices[i] = link.choiceText.getText();
            outcomes.addAll(link.outcomeList.stream().map(TextField::getText).collect(Collectors.toList()));
            chances.addAll(link.chanceList.stream().map(field -> Integer.parseInt(field.getText())).collect(Collectors.toList()));

            event.outcomes.add(outcomes);
            event.chances.add(chances);
            event.resultingAction.add(actions);

            i++;
        }

        event.resultingAction = new ArrayList<>();
        if(actionList.size() > 0) {
            for (ArrayList<TextField> list : actionList) {
                ArrayList<String> paramList = new ArrayList<>();
                event.resultingAction.add(paramList);
                paramList.addAll(list.stream().map(TextField::getText).collect(Collectors.toList()));
            }
        }

        eventMap.put(event.name, event);
    }

    public void writeToJson(String fileName){
        try {
            String currPath = new File("").getAbsolutePath();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(currPath+"/"+fileName), eventMap.values());

//            String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public class JsonEvent{
        public Boolean root = false;
        public String title, name;
        public String[] description;
        public String[] choices;
        public ArrayList<ArrayList<String>> outcomes = new ArrayList<>();
        public ArrayList<ArrayList<Integer>> chances = new ArrayList<>();
        public ArrayList<ArrayList<String>> resultingAction = new ArrayList<>();
    }
}
