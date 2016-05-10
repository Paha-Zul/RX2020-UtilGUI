import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by Paha on 4/2/2016.
 */
public class ActivityController {

    public TextField nameField;
    public TextField buttonTitleField;
    public TextArea descTextArea;
    public ComboBox<String> activityComboBox;

    public ArrayList<ArrayList<TextField>> actionList = new ArrayList<>();
    public ArrayList<TextField> restrictionList = new ArrayList<>();

    public HashMap<String, SearchActivityJSON> activityMap = new HashMap<>();
    HashMap<String, String> fileMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    private File currFile;

    public void resetControllerLists(){
        actionList = new ArrayList<>();
        restrictionList = new ArrayList<>();
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

    public boolean loadSearchActivities(String fileName){
        File file = new File(fileName);
        this.currFile = file;
        try {
            SearchActivityJSON[] activityJSONs = mapper.readValue(file, SearchActivityJSON[].class);
            for(SearchActivityJSON act : activityJSONs)
                activityMap.put(act.name, act);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void save(){
        SearchActivityJSON act = new SearchActivityJSON();
        act.name = nameField.getText();
        act.buttonTitle = buttonTitleField.getText();
        act.description = descTextArea.getText();

        act.action = new ArrayList<>();
        for(ArrayList<TextField> list : actionList){
            ArrayList<String> paramList = new ArrayList<>();
            act.action.add(paramList);
            paramList.addAll(list.stream().map(TextField::getText).collect(Collectors.toList()));
        }

        act.restrictions = new ArrayList<>();
        act.restrictions.addAll(restrictionList.stream().map(TextField::getText).collect(Collectors.toList()));

        activityMap.put(act.name, act);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(this.currFile, activityMap.values());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class SearchActivityJSON{
        public String name;
        public String buttonTitle;
        public ArrayList<ArrayList<String>> action;
        public String description;
        public ArrayList<String> restrictions;
    }
}
