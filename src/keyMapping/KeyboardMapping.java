package keyMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 
 * @author Abhishek B
 * @author Safkat Islam
 * 
 */

public class KeyboardMapping {

	private static final int NUM_KEYS_IN_ROW = 14;
	private static final int NUM_KEYS_IN_COLUMN = 5;
	private static final double KEY_WIDTH = 100;
	private static final double KEY_HEIGHT = 100;
	private static final double WIDTH = KEY_WIDTH * NUM_KEYS_IN_ROW;
	private static final double HEIGHT = KEY_HEIGHT * NUM_KEYS_IN_COLUMN;
	private GridPane myGrid;
	private Stage myStage;
	private Map<KeyCode, String> myButtonFunctionsMap;
	private MappedKeys myMappedKeys;
	private KeyMapForm myKeyMapForm;

	public KeyboardMapping(Map<KeyCode, String> keyFunctions) {
		myMappedKeys = new MappedKeys();
		myButtonFunctionsMap = new HashMap<KeyCode, String>();
		List<String> functionList = new ArrayList<String>(myButtonFunctionsMap.values());
		myKeyMapForm = new KeyMapForm(comboBoxForButton(functionList), this);
	}

	public void createKeyboardView() {
		myStage = new Stage();
		myStage.setTitle("Vooga Salad Bits Please Keyboard Mapping Utility");
		myGrid = new GridPane();
		Scene myScene = new Scene(myGrid, WIDTH, HEIGHT);
		myScene.getStylesheets().add(getClass().getResource("keyboard.css").toExternalForm());
		myStage.setScene(myScene);
		buildKeyboard();
	}

	public void buildKeyboard() {
		int num = 0;
		for(int i = 0; i < NUM_KEYS_IN_COLUMN; i++)
		{
			for(int j = 0; j < NUM_KEYS_IN_ROW; j++)
			{
				num = buildButton(num, i, j);
			}
		}
		myStage.show();
	}

	private int buildButton(int num, int i, int j) {
		StackPane sp = new StackPane();
		String key = this.myMappedKeys.getKey(num);
		Button button = new Button(key);
		button.setId("keyButton");
		button.setPrefSize(KEY_WIDTH, KEY_HEIGHT);
		button.setDisable(key == "");
		button.setOnAction((event) -> {
			myKeyMapForm.createKeyMapForm(key);
		});
		sp.getChildren().add(button);
		myGrid.add(sp, j, i);
		num++;
		return num;
	}

	private ComboBox<String> comboBoxForButton(List<String> functionList) {
		ComboBox<String> functionCombos = new ComboBox<String>();
		for(String function : functionList) {
			functionCombos.getItems().add(function);
		}
		return functionCombos;
	}

	public void setNewKey(String button, String keyFunction)
	{
		KeyCode kc = this.myMappedKeys.getKeyCode(button);
		myButtonFunctionsMap.put(kc, keyFunction);
	}
}