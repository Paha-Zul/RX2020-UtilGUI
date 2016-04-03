import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Controller {
    HashMap<String, JsonEvent> eventMap = new HashMap<>();
    public ArrayList<ArrayList<TextField>> actionList = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private JsonEvent event;

    public CheckBox isRoot;
    public TextField title, name;

    public ArrayList<TextArea> descList = new ArrayList<>();
    public ArrayList<ChoiceLink> choiceList = new ArrayList<>();

    public void reset(){
        descList = new ArrayList<>();
        choiceList = new ArrayList<>();
    }

    public void loadEventList(String fileName, Controller controller) {
        String currPath = new File("").getAbsolutePath();

        File eventFile = new File(currPath + "/"+fileName);
        if (!eventFile.isDirectory() && eventFile.exists()){
            try {
                JsonEvent[] events = mapper.readValue(eventFile, JsonEvent[].class);
                for (JsonEvent evt : events)
                    controller.eventMap.put(evt.name, evt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    public void makeIntoPOJO(Controller controller){
        event = new JsonEvent();
        event.root = controller.isRoot.isSelected();
        event.title = controller.title.getText();
        event.name = controller.name.getText();

        /*
         * Record all the descriptions. Only use child 3+
         */
        ArrayList<TextArea> descList = controller.descList;
        event.description = new String[descList.size()];
        for(int i=0;i<descList.size();i++) //Record all the text.
            event.description[i] = descList.get(i).getText();


        /*
         * Record all the choices. Complicated much very.
         */
        int i=0;
        ArrayList<ChoiceLink> choiceList = controller.choiceList;
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

        controller.eventMap.put(event.name, event);
        //writeToJson();
    }

    public void writeToJson(Controller controller, String fileName){
        try {
            String currPath = new File("").getAbsolutePath();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(currPath+"/"+fileName), controller.eventMap.values());

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
