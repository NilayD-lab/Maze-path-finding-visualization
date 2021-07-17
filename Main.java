import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ChoiceBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;

import javafx.scene.layout.BorderStroke;

import javax.imageio.plugins.tiff.GeoTIFFTagSet;

public class Main extends Application {
    private static ArrayList<Maze> mazes = new ArrayList<>();
    private static ArrayList<Button> buttons=  new ArrayList<>();
    private Button delete;
    private Button makeBoard;
    private Scene scene;
    private static int  count=0;
    private boolean deleteOn =false;
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) throws Exception{
        BorderPane pane = new BorderPane();
        HBox hBox = new HBox();
        pane.setPadding(new Insets(100, 10, 10, 10));
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(50, 10, 10, 10));
        gridPane.setHgap(15);
        gridPane.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(pane, 500, 500);
        makeBoard = new Button("Make Board");
        makeBoard.setOnAction(e->{
            if (count!=5) {
                openForm(count, gridPane);
            }
        });
        delete = new Button("Delete Maze");
        delete.setOnAction(e->{
            delete.setText("Cancel");
            delete(gridPane);
        });
        hBox.getChildren().addAll(makeBoard, delete);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(50);
        pane.setTop(hBox);
        pane.setCenter(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();
        //width.deselect();


    }
    public void delete(GridPane gridPane){
        for (int j=0;j<buttons.size();j++){
            buttons.get(j).setOnAction(e->{
                for (int i=0;i< buttons.size();i++){
                    if (buttons.get(i).equals(e.getTarget())) {
                        for (int z=i+1;z<buttons.size();z++){
                            GridPane.setColumnIndex(buttons.get(z), GridPane.getColumnIndex(buttons.get(z))-1);
                        }
                        gridPane.getChildren().remove(buttons.get(i));
                        buttons.remove(buttons.get(i));
                        mazes.remove(i);
                        gridPane.setOnMouseClicked(c-> System.out.print(""));
                        count--;
                        break;
                    }
                }
            });
        }
        delete.setOnAction(e->{
            delete.setText("Delete Maze");
            for (int i=0;i<buttons.size();i++){
                set(i);
            }
            delete.setOnAction(c->{
                delete.setText("Cancel");
                delete(gridPane);
            });
            makeBoard.setOnAction(c->openForm(count, gridPane));
        });
        makeBoard.setOnAction(e->{
            System.out.print("");
        });
    }
    public static void open(int index, GridPane gridPane){
        buttons.get(index).setOnAction(c->{
            mazes.get(index).getStage().show();
        });
        GridPane.setConstraints(buttons.get(index), index, 3);
        gridPane.getChildren().add(buttons.get(index));
    }
    public void set(int index){
        buttons.get(index).setOnAction(e->{
            buttons.get(index).setOnAction(c->{
                mazes.get(index).getStage().show();
            });
        });
    }
    public void openForm(int num, GridPane grid){
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        BorderPane pane = new BorderPane();
        HBox hBox = new HBox();
        Hyperlink link = new Hyperlink("Need help finding colors?");
        link.setOnAction(e-> {
            if (Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URI("https://htmlcolorcodes.com/"));
                }
                catch (Exception e1){

                }
            }
        });
        hBox.getChildren().add(link);
        hBox.setAlignment(Pos.TOP_LEFT);
        pane.setBottom(hBox);
        pane.setPadding(new Insets(10, 10, 10, 10));
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(20);
        gridPane.setHgap(5);
        Scene scene = new Scene(pane, 550, 550);

        Text background = new Text("Background Color");
        GridPane.setConstraints(background, 0, 0);
        TextField backgroundInput = new TextField("#282828");
        GridPane.setConstraints(backgroundInput, 1, 0);

        Text border = new Text("Border Color");
        GridPane.setConstraints(border, 0, 1);
        TextField borderInput = new TextField("#000000");
        GridPane.setConstraints(borderInput, 1, 1);
        Text fill = new Text("Fill Color");
        GridPane.setConstraints(fill, 0, 2);
        TextField fillInput = new TextField("#E96713");
        GridPane.setConstraints(fillInput, 1, 2);
        Text path = new Text("Path Color");
        GridPane.setConstraints(path, 0, 3);
        TextField pathInput = new TextField("#FFC300");
        GridPane.setConstraints(pathInput, 1, 3);

        Text start = new Text("Start Node Color");
        GridPane.setConstraints(start, 0, 4);
        TextField startInput= new TextField("#FFFFFF");
        GridPane.setConstraints(startInput, 1, 4);
        Text end = new Text("End Node Color");
        GridPane.setConstraints(end, 0, 5);
        TextField endInput = new TextField("#C60000");
        GridPane.setConstraints(endInput, 1, 5);


        Text width2 = new Text("Maze Width");
        GridPane.setConstraints(width2, 0, 7);
        TextField width = new TextField();
        width.setPromptText("Odd # only");
        GridPane.setConstraints(width, 1, 7);

        Text height2 = new Text("Maze Height");
        GridPane.setConstraints(height2, 0, 8);
        TextField height = new TextField();
        height.setPromptText("Odd # only");
        GridPane.setConstraints(height, 1, 8);
        //rect size

        Slider size = new Slider(18, 45, 25);
        size.setShowTickMarks(true);
        GridPane.setConstraints(size, 0, 6);
        Rect sample = new Rect(Color.web(borderInput.getText()), Color.web(backgroundInput.getText()), 0, scene, 0, 0, (int)size.getValue());
        sample.setColor();
        //GridPane.setConstraints(sample, 1, 8);

        GridPane.setRowIndex(sample, 9);
        GridPane.setColumnIndex(sample, 1);
        GridPane.setMargin(sample, new Insets(10, 10, 10, 60));
        TextField showSize = new TextField();
        showSize.setText(""+(int)size.getValue());

        Text pixelWidth = new Text("N/A");
        GridPane.setConstraints(pixelWidth, 2, 7);
        Text pixelHeight = new Text("N/A");
        GridPane.setConstraints(pixelHeight, 2, 8);
        showSize.setOnKeyReleased(e->{
            if (!showSize.getText().equals("") && Integer.parseInt(showSize.getText())<=45 && Integer.parseInt(showSize.getText())>=18){
                sample.setWidth(Integer.parseInt(showSize.getText()));
                sample.setHeight(Integer.parseInt(showSize.getText()));
                showSize.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                size.adjustValue(Integer.parseInt(showSize.getText()));
                showSize.positionCaret(2);
                if (!width.getText().equals("")) {
                    if (Integer.parseInt(width.getText())%2==1 && (int) size.getValue() * Integer.parseInt(width.getText()) +150 < 1920 && (int) size.getValue() * Integer.parseInt(width.getText()) > 500) {
                        pixelWidth.setText("(" + ((int) size.getValue() * Integer.parseInt(width.getText()) +150) + " pixels)");
                        pixelWidth.setFill(Color.BLACK);
                        width.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                    } else {
                        pixelWidth.setText("(" + ((int) size.getValue() * Integer.parseInt(width.getText())) + " pixels)");
                        pixelWidth.setFill(Color.web("#B22222"));
                        width.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    }
                }
                if (!height.getText().equals("")){
                    if (Integer.parseInt(height.getText())%2==1 && (int)size.getValue() * Integer.parseInt(height.getText())+55<1080 && (int)size.getValue() * Integer.parseInt(height.getText())>500){
                        pixelHeight.setText("("+ ((int)size.getValue() * Integer.parseInt(height.getText())+55) +" pixels)");
                        pixelHeight.setFill(Color.BLACK);
                        height.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                    }
                    else {
                        pixelHeight.setText("("+ ((int)size.getValue() * Integer.parseInt(height.getText())+55) +" pixels)");
                        pixelHeight.setFill(Color.web("#B22222"));
                        height.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    }
                }

            }
            else {
                showSize.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        size.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                showSize.setText(""+(int)(double)new_val);
                sample.setWidth((double)new_val);
                sample.setHeight((double)new_val);
                if (!width.getText().equals("")) {
                    if (Integer.parseInt(width.getText())%2==1 && (int) size.getValue() * Integer.parseInt(width.getText()) +150 < 1920 && (int) size.getValue() * Integer.parseInt(width.getText()) > 500) {
                        pixelWidth.setText("(" + ((int) size.getValue() * Integer.parseInt(width.getText()) +150) + " pixels)");
                        pixelWidth.setFill(Color.BLACK);
                        width.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                    } else {
                        pixelWidth.setText("(" + ((int) size.getValue() * Integer.parseInt(width.getText()) +150) + " pixels)");
                        pixelWidth.setFill(Color.web("#B22222"));
                        width.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    }
                }
                if (!height.getText().equals("")){
                    if (Integer.parseInt(height.getText())%2==1 && (int)size.getValue() * Integer.parseInt(height.getText())+55<1080 && (int)size.getValue() * Integer.parseInt(height.getText())>500){
                        pixelHeight.setText("("+ ((int)size.getValue() * Integer.parseInt(height.getText())+55) +" pixels)");
                        pixelHeight.setFill(Color.BLACK);
                        height.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                    }
                    else {
                        pixelHeight.setText("("+ ((int)size.getValue() * Integer.parseInt(height.getText())+55) +" pixels)");
                        pixelHeight.setFill(Color.web("#B22222"));
                        height.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    }
                }
            }
        });
        size.setPrefWidth(80);
        GridPane.setConstraints(showSize, 1, 6);
        Button finish = new Button("Finish");
        GridPane.setConstraints(finish, 0, 10);
        //choice box
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        GridPane.setConstraints(choiceBox, 0, 9);
        choiceBox.getItems().addAll("Start Node", "End Node", "Border Node", "Fill", "Path");
        choiceBox.setValue("Border Node");
        choiceBox.setOnAction(e->{
            if (choiceBox.getValue().equals("Start Node")){
                try {
                    sample.setFill(Color.web(startInput.getText()));
                    sample.setToNothing();
                } catch (Exception c){
                    startInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }

            }
            if (choiceBox.getValue().equals("End Node")){
                try {
                    sample.setFill(Color.web(endInput.getText()));
                    sample.setToNothing();
                } catch (Exception c){
                    endInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }

            }
            if (choiceBox.getValue().equals("Border Node")){
                try {
                    sample.setFill(Color.web(backgroundInput.getText()));
                    sample.setOn(false);
                    sample.setColor();
                } catch (Exception c){
                    backgroundInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }

            }
            if (choiceBox.getValue().equals("Fill")){
                try {
                    sample.setFill(Color.web(fillInput.getText()));
                    sample.setToNothing();
                } catch (Exception c){
                    fillInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }

            }
            if (choiceBox.getValue().equals("Path")){
                try {
                    sample.setFill(Color.web(pathInput.getText()));
                    sample.setToNothing();
                } catch (Exception c){
                    pathInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }

            }
        });
        backgroundInput.setOnKeyReleased(e->{
            try {
                sample.setFill(Color.web(backgroundInput.getText()));
                sample.setOffColor(Color.web(backgroundInput.getText()));
                sample.setColor();
                sample.setOn(false);
                choiceBox.setValue("Border Node");
                backgroundInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                backgroundInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        backgroundInput.setOnMouseClicked(e->{
            try {
                sample.setFill(Color.web(backgroundInput.getText()));
                sample.setOffColor(Color.web(backgroundInput.getText()));
                sample.setColor();
                sample.setOn(false);
                choiceBox.setValue("Border Node");
                backgroundInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                backgroundInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        borderInput.setOnKeyReleased(e->{
            try {
                sample.setFill(Color.web(borderInput.getText()));
                sample.setOnColor(Color.web(borderInput.getText()));
                sample.setColor();
                sample.setOn(true);
                choiceBox.setValue("Border Node");
                borderInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                borderInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        borderInput.setOnMouseClicked(e->{
            try {
                sample.setFill(Color.web(borderInput.getText()));
                sample.setOnColor(Color.web(borderInput.getText()));
                sample.setColor();
                sample.setOn(true);
                choiceBox.setValue("Border Node");
                borderInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                borderInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        startInput.setOnKeyReleased(e->{
            try {
                sample.setFill(Color.web(startInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("Start Node");
                startInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                startInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        startInput.setOnMouseClicked(e->{
            try {
                sample.setFill(Color.web(startInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("Start Node");
                startInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                startInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        endInput.setOnKeyReleased(e->{
            try {
                sample.setFill(Color.web(endInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("End Node");
                endInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                endInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        endInput.setOnMouseClicked(e->{
            try {
                sample.setFill(Color.web(endInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("End Node");
                endInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                endInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        fillInput.setOnKeyReleased(e->{
            try {
                sample.setFill(Color.web(fillInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("Fill");
                fillInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                fillInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        fillInput.setOnMouseClicked(e->{
            try {
                sample.setFill(Color.web(fillInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("Fill");
                fillInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                fillInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        pathInput.setOnKeyReleased(e->{
            try {
                sample.setFill(Color.web(pathInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("Fill");
                pathInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                pathInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        pathInput.setOnMouseClicked(e->{
            try {
                sample.setFill(Color.web(pathInput.getText()));
                sample.setToNothing();
                choiceBox.setValue("Fill");
                pathInput.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
            } catch (Exception c){
                pathInput.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        width.setOnKeyReleased(e->{
            try {
                if (Integer.parseInt(width.getText())%2==1 && (int)size.getValue() * Integer.parseInt(width.getText()) +150 <1920 && (int)size.getValue() * Integer.parseInt(width.getText())>500){
                    pixelWidth.setText("("+ ((int)size.getValue() * Integer.parseInt(width.getText()) +150) +" pixels)");
                    pixelWidth.setFill(Color.BLACK);
                    width.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                }
                else {
                    pixelWidth.setText("("+ ((int)size.getValue() * Integer.parseInt(width.getText()) +150) +" pixels)");
                    pixelWidth.setFill(Color.web("#B22222"));
                    width.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }
            } catch (Exception c){
                pixelWidth.setText("N/A");
                pixelWidth.setFill(Color.web("#B22222"));
                width.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }

        });
        height.setOnKeyReleased(e->{
            try {
                if (Integer.parseInt(height.getText())%2==1 && (int)size.getValue() * Integer.parseInt(height.getText())+55<1080 && (int)size.getValue() * Integer.parseInt(height.getText())>500){
                    pixelHeight.setText("("+ ((int)size.getValue() * Integer.parseInt(height.getText())+55) +" pixels)");
                    pixelHeight.setFill(Color.BLACK);
                    height.setStyle("-fx-text-box-border: #4EBA36; -fx-focus-color: #4EBA36;");
                }
                else {
                    pixelHeight.setText("("+ ((int)size.getValue() * Integer.parseInt(height.getText())+55) +" pixels)");
                    pixelHeight.setFill(Color.web("#B22222"));
                    height.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                }
            } catch (Exception c){
                pixelHeight.setText("N/A");
                pixelHeight.setFill(Color.web("#B22222"));
                height.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            }
        });
        finish.setOnAction(e->{
            try {
                boolean notEmpty = !backgroundInput.getText().equals("") && !borderInput.getText().equals("") && !startInput.getText().equals("") && !endInput.getText().equals("") &&!height.getText().equals("") &&!width.getText().equals("") &&!fillInput.getText().equals("") && !showSize.getText().equals("") && !pathInput.getText().equals("");
                boolean requirements = size.getValue()>=14 && size.getValue()<=45 && Integer.parseInt(width.getText())%2==1 && (int)size.getValue() * Integer.parseInt(width.getText()) +150<1920 && (int)size.getValue() * Integer.parseInt(width.getText())>500 && Integer.parseInt(height.getText())%2==1 && (int)size.getValue() * Integer.parseInt(height.getText())+55<1080 && (int)size.getValue() * Integer.parseInt(height.getText())>500;
                if (notEmpty && requirements){
                    mazes.add(new Maze(Integer.parseInt(height.getText()), Integer.parseInt(width.getText()), new Stage(), (int)size.getValue(), Color.web(borderInput.getText()), Color.web(backgroundInput.getText()), Color.web(startInput.getText()), Color.web(endInput.getText()), Color.web(fillInput.getText()), Color.web(pathInput.getText())));
                    mazes.get(num).makeBoard();
                    buttons.add(new Button("Maze "+(num+1)));
                    open(num, grid);
                    count++;
                    stage.close();
                }
            } catch (Exception c){
                System.out.println(c);
            }
        });
        gridPane.getChildren().addAll(width2, height2, size, height, finish, showSize, width, sample, background, backgroundInput, border, borderInput, fill, fillInput, start, startInput, end, endInput, choiceBox, pixelWidth, pixelHeight, path, pathInput);
        pane.setCenter(gridPane);
        stage.setScene(scene);
        stage.showAndWait();
    }







}