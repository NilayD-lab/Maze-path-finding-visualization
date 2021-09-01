import com.sun.javafx.font.directwrite.RECT;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class Maze {
    private Slider slider = new Slider(930, 1000, 970);
    private ArrayList<Rect> pathList;
    private int squareLength;
    private int seconds=0;
    private ArrayList<Integer> sizes = new ArrayList<>();
    private ArrayList<Rect> rectsList = new ArrayList<>();
    private ArrayList<Rect> endRectsList = new ArrayList<>();
    private ArrayList<ArrayList<Rect>> groupList = new ArrayList<>();
    private boolean finished = false;
    private int row;
    private int col;
    private Boolean[][] visited;
    private Rect[][] rects;
    private BorderPane pane;
    private Scene scene;
    private Stage stage;
    private boolean hasStartNode;
    private boolean hasEndNode;
    private Color borderColor;
    private Color backgroundColor;
    private Color startColor;
    private Color endColor;
    private Color fillColor;
    private Color pathColor;
    private boolean goNext = false;
    public Maze(int r, int c, Stage s, int len, Color boColor, Color backColor, Color sColor, Color eColor, Color fColor, Color pColor){
        rects = new Rect[r][c];
        pane = new BorderPane();
        stage = s;
        row=r;
        col=c;
        pathList = new ArrayList<>();
        visited = new Boolean[row][col];
        for (int i=0;i<visited.length;i++){
            for (int j=0;j<visited[0].length;j++){
                visited[i][j] = false;
            }
        }
        squareLength=len;
        hasEndNode = false;
        hasStartNode = false;
        borderColor = boColor;
        backgroundColor = backColor;
        startColor = sColor;
        endColor = eColor;
        fillColor = fColor;
        pathColor = pColor;
    }
    public void makeBoard(){
        int num=0;
        pane = new BorderPane();
        scene = new Scene(pane, col*squareLength + 150, row*squareLength+30);
        stage.setTitle("Maze");
        HBox options = new HBox();
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("Dijkstra's", "A*", "Bi-Directional BFS");
        choiceBox.setValue("Dijkstra's");
        Button goal = new Button("Goal");
        goal.setOnAction(e-> {pickEndNode();});
        Button start = new Button("Start");
        start.setOnAction(e-> pickStartNode());
        Button border = new Button("Borders");
        border.setOnAction(e->activateBorders());
        Button play = new Button("Play");
        play.setOnAction(e-> {
            if (hasStartNode && hasEndNode){
                play(choiceBox.getValue());
            }
        });
        Button clear = new Button("Clear All");
        clear.setOnAction(e->{clear();});
        Button clean = new Button("Clean");
        clean.setOnAction(e->{
            rectsList.clear();
            pathList.clear();
            endRectsList.clear();
            for (int r=0;r<rects.length;r++){
                for (int c=0;c<rects[r].length;c++){
                    if (rects[r][c].getFill()!=borderColor && rects[r][c].getFill()!=startColor && rects[r][c].getFill()!=endColor){
                        rects[r][c].setFill(backgroundColor);
                    }
                }
            }
        });
        options.getChildren().addAll(start,goal, border,choiceBox, play, clear, clean);
        options.setMinHeight(60);
        pane.setTop(options);
        for (int r=0;r<row;r++){
            for (int c=0;c<col;c++){
                rects[r][c] = new Rect(borderColor, backgroundColor, Integer.MAX_VALUE, scene, c*squareLength, (r)*squareLength+25, squareLength);
                rects[r][c].setColor();
                pane.getChildren().add(rects[r][c]);
                num++;
            }
        }
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        pane.setRight(vbox);
        Text slow = new Text(slider.getLayoutX(), slider.getLayoutY(), "slow                               fast");
        Button maze = new Button("Make Maze");
        maze.setOnAction(e->{
            clear();
            for (int i=0;i<visited.length;i++){
                for (int j=0;j<visited[0].length;j++){
                    visited[i][j] = false;
                }
            }
            seconds=0;
            recursiveBackTracking(1, 1, 0);
            rectsList.clear();
            ArrayList<Rect> temp = new ArrayList<>();
            ArrayList<Rect> temp2 = new ArrayList<>();
            int thing =0;
            for (int r=0;r<rects.length;r++){
                for (int c=0;c<rects[r].length;c++){
                    temp.add(rects[r][c]);
                }
                temp2 = new ArrayList<>(temp);
                groupList.add(temp2);
                temp.clear();;
            }
            translate();
        });
        Button edit = new Button("Edit Maze");
        edit.setOnAction(e->editMaze());
        vbox.getChildren().addAll(slider, slow, maze, edit);
        stage.setScene(scene);
        stage.show();
    }
    public void editMaze(){
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
        TextField backgroundInput = new TextField(backgroundColor.toString().toUpperCase());
        GridPane.setConstraints(backgroundInput, 1, 0);
        Text border = new Text("Border Color");
        GridPane.setConstraints(border, 0, 1);
        TextField borderInput = new TextField(borderColor.toString().toUpperCase());
        GridPane.setConstraints(borderInput, 1, 1);
        Text fill = new Text("Fill Color");
        GridPane.setConstraints(fill, 0, 2);
        TextField fillInput = new TextField(fillColor.toString().toUpperCase());
        GridPane.setConstraints(fillInput, 1, 2);
        Text path = new Text("Path Color");
        GridPane.setConstraints(path, 0, 3);
        TextField pathInput = new TextField(pathColor.toString().toUpperCase());
        GridPane.setConstraints(pathInput, 1, 3);

        Text start = new Text("Start Node Color");
        GridPane.setConstraints(start, 0, 4);
        TextField startInput= new TextField(startColor.toString().toUpperCase());
        GridPane.setConstraints(startInput, 1, 4);
        Text end = new Text("End Node Color");
        GridPane.setConstraints(end, 0, 5);
        TextField endInput = new TextField(endColor.toString().toUpperCase());
        GridPane.setConstraints(endInput, 1, 5);
        Rect sample = new Rect(Color.web(borderInput.getText()), Color.web(backgroundInput.getText()), 0, scene, 0, 0, squareLength);
        sample.setColor();
        GridPane.setConstraints(sample, 1, 6);
        GridPane.setMargin(sample, new Insets(10, 10, 10, 60));
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        GridPane.setConstraints(choiceBox, 0, 6);
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
        Button save = new Button("Save");
        GridPane.setConstraints(save, 0, 10);
        save.setOnAction(e->{
            try {
                boolean notEmpty = !backgroundInput.getText().equals("") && !borderInput.getText().equals("") && !startInput.getText().equals("") && !endInput.getText().equals("") && !pathInput.getText().equals("");
                if (notEmpty){

                    for (int r=0;r<rects.length;r++){
                        for (int c=0;c<rects[r].length;c++){
                            if (rects[r][c].getFill()!=borderColor && rects[r][c].getFill()!=startColor && rects[r][c].getFill()!=endColor){
                                rects[r][c].setFill(backgroundColor);
                            }
                        }
                    }
                    ArrayList<Rect> borderList = new ArrayList<>(findRects(borderColor));
                    ArrayList<Rect> backgroundList = new ArrayList<>(findRects(backgroundColor));
                    ArrayList<Rect> startList = new ArrayList<>(findRects(startColor));
                    ArrayList<Rect> endList = new ArrayList<>(findRects(endColor));
                    borderColor = Color.web(borderInput.getText());
                    backgroundColor = Color.web(backgroundInput.getText());
                    startColor = Color.web(startInput.getText());
                    endColor = Color.web(endInput.getText());
                    changeColor(borderList, Color.web(borderInput.getText()));
                    changeColor(backgroundList, Color.web(backgroundInput.getText()));
                    changeColor(startList, Color.web(startInput.getText()));
                    changeColor(endList, Color.web(endInput.getText()));
                    fillColor = Color.web(fillInput.getText());
                    pathColor = Color.web(pathInput.getText());
                    stage.close();
                }
            } catch (Exception c){
                System.out.println("error");
            }
        });
        gridPane.getChildren().addAll(save, sample, background, backgroundInput, border, borderInput, fill, fillInput, start, startInput, end, endInput, choiceBox, path, pathInput);
        pane.setCenter(gridPane);
        stage.setScene(scene);
        stage.showAndWait();
    }
    public ArrayList<Rect> findRects(Color color){
        ArrayList<Rect> list = new ArrayList<>();
        for (int r=0;r<rects.length;r++){
            for (int c=0;c<rects[r].length;c++){
                if (rects[r][c].getFill()==color){
                    list.add(rects[r][c]);
                }
            }
        }
        return list;
    }
    public void changeColor(ArrayList<Rect> list, Color color){
        for (int r=0;r<rects.length;r++) {
            for (int c = 0; c < rects[r].length; c++) {
                if (list.contains(rects[r][c])){
                    rects[r][c].setFill(color);
                }
            }
        }
    }
    public void clear(){
        groupList.clear();
        pathList.clear();
        rectsList.clear();
        int num=0;
        for (int r=0;r<row;r++){
            for (int c=0;c<col;c++){
                rects[r][c] = new Rect(borderColor, backgroundColor, Integer.MAX_VALUE, scene, c*squareLength, (r)*squareLength+25, squareLength);
                rects[r][c].setColor();
                pane.getChildren().add(rects[r][c]);
                num++;
            }
        }
        hasStartNode = false;
        hasEndNode = false;
    }
    public void print(Boolean[][] arr){
        for (Boolean[] r: arr){
            for (Boolean c: r){
                System.out.print(c +" ");
            }
            System.out.println();
        }
    }
    public void play(String choice){
        if (choice.equals("Dijkstra's")){
            dijkstraAlgoth();
        }
        if (choice.equals("A*")){
            aStarAlgorithm();
        }
        if (choice.equals("Bi-Directional BFS")) {
            bidirectionalBFS();
        }



    }
    public void recursiveBackTracking(int row, int col, int index){
        if (!rectsList.contains(rects[row][col])) {
            pathList.add(rects[row][col]);
            rectsList.add(index, rects[row][col]);
        }
        visited[row][col] = true;
        int num;
        boolean notFound=true;
        boolean options = false;
        if (row!=1 && !visited[row-2][col]){
            options = true;

        }
        else if (col!=visited[0].length-2 && !visited[row][col+2]){
            options = true;

        }
        else if (row!= visited.length-2 && !visited[row+2][col]){
            options = true;

        }
        else if (col!=1 && !visited[row][col-2]){
            options = true;

        }
        if (!options){
            //System.out.println(options);
            if (index==0){
                return;
            }
            //System.out.println(rectsList.get(index-1).getR()+ " "+ rectsList.get(index-1).getC() +" "+(index-1));
            //System.out.println(rectsList.get(index));
            recursiveBackTracking(rectsList.get(index-1).getR(), rectsList.get(index-1).getC(), index-1);
        }
        else {
            /*
            if (!rectsList.contains(rects[row][col])) {
                pathList.add(rects[row][col]);
                rectsList.add(rects[row][col]);
            }
            visited[row][col] = true;

             */
            while (notFound) {
                num = (int) (Math.random()*4);
                if (num == 0 && row != 1 && !visited[row - 2][col]) {
                    pathList.add(rects[row-1][col]);
                    visited[row - 1][col] = true;
                    notFound = false;
                    recursiveBackTracking(row - 2, col, index + 1);
                    break;

                } else if ((num == 1) && col != visited[0].length - 2 && !visited[row][col + 2]) {
                    pathList.add(rects[row][col+1]);
                    visited[row][col + 1] = true;
                    notFound = false;
                    recursiveBackTracking(row, col + 2, index + 1);
                    break;

                } else if (num == 2 && row != visited.length - 2 && !visited[row + 2][col]) {
                    pathList.add(rects[row+1][col]);
                    visited[row + 1][col] = true;
                    notFound = false;
                    recursiveBackTracking(row + 2, col, index + 1);

                } else if (num==3&&col != 1 && !visited[row][col - 2]) {
                    pathList.add(rects[row][col-1]);
                    visited[row][col - 1] = true;
                    notFound = false;
                    recursiveBackTracking(row, col - 2, index + 1);

                }
                seconds++;
            }
        }
    }
    public void translate() {
        seconds=0;
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                if (finished) {
                    if (seconds == pathList.size()) {
                        finished=false;
                        ses.shutdown();
                        return;
                    }
                    pathList.get(seconds).setFill(backgroundColor);
                    seconds++;
                }
                else {
                    if (seconds==groupList.size()){
                        finished=true;
                        seconds=0;
                    }
                    else {
                        for (int i = 0; i < groupList.get(seconds).size(); i++) {
                            groupList.get(seconds).get(i).setFill(borderColor);
                        }
                        seconds++;
                    }
                }

            }
        }, 0, 7, TimeUnit.MILLISECONDS);
    }


    public int distance(int r, int c, int startNodeRow, int startNodeCol){
        return Math.abs(r-startNodeRow) + Math.abs(c-startNodeCol);
    }
    public boolean searchAStar(int row, int col){
        if (rects[row][col].getFill()==startColor) {
            return true;
        }
        pathList.add(rects[row][col]);
        return searchAStar(rects[row][col].getTheParent().getR(), rects[row][col].getTheParent().getC());
    }

    public boolean contains(ArrayList<Rect> list, Rect rect){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getC()==rect.getC() && list.get(i).getR() == rect.getR()){
                return true;
            }
        }
        return false;
    }

    public boolean isValid(int r, int c){
        return notOutOfBounds(r, c) && (rects[r][c].getFill()==backgroundColor || rects[r][c].getFill()==endColor) && !rectsList.contains(rects[r][c]);
    }
    public boolean isBiValidStart(int r, int c){
        return notOutOfBounds(r, c) && (rects[r][c].getFill()==backgroundColor) && !rectsList.contains(rects[r][c]) ;
    }
    public boolean isBiValidEnd(int r, int c){
        return  notOutOfBounds(r, c) && rects[r][c].getFill()==backgroundColor && !endRectsList.contains(rects[r][c]);
    }
    public boolean notOutOfBoundsRec(int r, int c){
        return r<row-1 && c<col-1 && r>=1 && c>=1;
    }
    public boolean notOutOfBounds(int r, int c){
        return r<row && c<col && r>=0 && c>=0;
    }
    public boolean search(Rect current, Rect end){
        if (current.getR()==end.getR() && current.getC()==end.getC()){
            pathList.remove(pathList.size()-1);
            //pathList.remove(0);
            return true;
        }
        int row = current.getR();
        int col = current.getC();
        if(notOutOfBounds(row-1, col) && rects[row-1][col].getNum()==current.getNum()-1 && rectsList.contains(rects[row-1][col])){
            pathList.add(rects[row-1][col]);
            return search(rects[row-1][col], end);
        }
        if(notOutOfBounds(row, col+1) && rects[row][col+1].getNum()==current.getNum()-1 && rectsList.contains(rects[row][col+1])){
            pathList.add(rects[row][col+1]);
            return search(rects[row][col+1], end);
        }
        if(notOutOfBounds(row+1, col) && rects[row+1][col].getNum()==current.getNum()-1 && rectsList.contains(rects[row+1][col])){
            pathList.add(rects[row+1][col]);
            return search(rects[row+1][col], end);
        }
        if(notOutOfBounds(row, col-1) && rects[row][col-1].getNum()==current.getNum()-1 && rectsList.contains(rects[row][col-1])){
            pathList.add(rects[row][col-1]);
            return search(rects[row][col-1], end);
        }
        return false;

    }
    public boolean search(int row,int col, int findRow, int findCol, int current, int[][] arr){
        if (row==findRow && col==findCol) {
            pathList.remove(pathList.size()-1);
            return true;
        }
        if(notOutOfBounds(row-1, col) && arr[row-1][col]==current-1 && rectsList.contains(rects[row-1][col])){
            pathList.add(rects[row-1][col]);
            return search(row-1, col, findRow, findCol, current-1, arr);
        }
        if(notOutOfBounds(row, col+1) && arr[row][col+1]==current-1 && rectsList.contains(rects[row][col+1])){
            pathList.add(rects[row][col+1]);
            return search(row, col+1, findRow, findCol, current-1, arr);
        }
        if(notOutOfBounds(row+1, col) && arr[row+1][col]==current-1 && rectsList.contains(rects[row+1][col])){
            pathList.add(rects[row+1][col]);
            return search(row+1, col, findRow, findCol, current-1, arr);
        }
        if(notOutOfBounds(row, col-1) && arr[row][col-1]==current-1 && rectsList.contains(rects[row][col-1])){
            pathList.add(rects[row][col-1]);
            return search(row, col-1, findRow, findCol, current-1, arr);
        }
        return false;
    }
    public boolean search(int row,int col, int findRow, int findCol, int current, int[][] arr, ArrayList<Rect> list){
        if (row==findRow && col==findCol) {
            pathList.remove(pathList.size()-1);
            return true;
        }
        if(notOutOfBounds(row-1, col) && arr[row-1][col]==current-1 && list.contains(rects[row-1][col])){
            pathList.add(rects[row-1][col]);
            return search(row-1, col, findRow, findCol, current-1, arr, list);
        }
        if(notOutOfBounds(row, col+1) && arr[row][col+1]==current-1 && list.contains(rects[row][col+1])){
            pathList.add(rects[row][col+1]);
            return search(row, col+1, findRow, findCol, current-1, arr, list);
        }
        if(notOutOfBounds(row+1, col) && arr[row+1][col]==current-1&& list.contains(rects[row+1][col])){
            pathList.add(rects[row+1][col]);
            return search(row+1, col, findRow, findCol, current-1, arr, list);
        }
        if(notOutOfBounds(row, col-1) && arr[row][col-1]==current-1&& list.contains(rects[row][col-1])){
            pathList.add(rects[row][col-1]);
            return search(row, col-1, findRow, findCol, current-1, arr, list);
        }
        return false;
    }
    public void bidirectionalBFS(){
        groupList = new ArrayList<>();
        endRectsList = new ArrayList<>();
        ArrayList<Rect> temp = new ArrayList<>();
        ArrayList<Rect> endRectsListTemp = new ArrayList<>();
        pathList = new ArrayList<>();
        finished = false;
        int[][] board = new int[row][col];
        int endNodeRow=0;
        int endNodeCol=0;
        int startNodeRow=0;
        int startNodeCol=0;
        int foundRow=0;
        int foundCol=0;
        int foundRow2 = -1;
        int foundCol2 = -1;
        sizes.clear();
        rectsList.clear();
        seconds=1;
        boolean foundEndnode = false;
        ArrayList<Rect> rectsListTemp = new ArrayList<>();
        for (int r=0;r<rects.length;r++){
            for (int c=0;c<rects[r].length;c++){
                if (rects[r][c].getFill()==startColor){
                    startNodeRow=r;
                    startNodeCol=c;
                    board[r][c] = -1;
                }
                if (rects[r][c].getFill()==endColor){
                    endNodeRow=r;
                    endNodeCol=c;
                    board[r][c] = -1;
                }
                if (rects[r][c].getFill()!=borderColor && rects[r][c].getFill()!=startColor && rects[r][c].getFill()!=endColor){
                    rects[r][c].setFill(backgroundColor);
                }
            }
        }
        for (int i=0;i<1;i++) {
            if (isBiValidStart(startNodeRow - 1, startNodeCol)) {
                if (board[startNodeRow-1][startNodeCol]==board[startNodeRow][startNodeCol]){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow - 1][startNodeCol]);
                board[startNodeRow-1][startNodeCol] = seconds;
            }
            if (isBiValidEnd(endNodeRow - 1, endNodeCol)) {
                if (board[endNodeRow-1][endNodeCol]==board[endNodeRow][endNodeCol]){
                    foundEndnode=true;
                    break;
                }
                endRectsListTemp.add(rects[endNodeRow - 1][endNodeCol]);
                board[endNodeRow-1][endNodeCol] = seconds;
            }
            if (isBiValidStart(startNodeRow, startNodeCol + 1)) {
                if (board[startNodeRow][startNodeCol + 1]==board[startNodeRow][startNodeCol]){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow][startNodeCol + 1]);
                board[startNodeRow][startNodeCol+1] = seconds;
            }
            if (isBiValidEnd(endNodeRow, endNodeCol + 1)) {
                if (board[endNodeRow][endNodeCol + 1]==board[endNodeRow][endNodeCol]){
                    foundEndnode=true;
                    break;
                }
                endRectsListTemp.add(rects[endNodeRow][endNodeCol + 1]);
                board[endNodeRow][endNodeCol+1] = seconds;
            }
            if (isBiValidStart(startNodeRow + 1, startNodeCol)) {
                if (board[startNodeRow+1][startNodeCol]==board[startNodeRow][startNodeCol]){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow + 1][startNodeCol]);
                board[startNodeRow+1][startNodeCol] = seconds;
            }
            if (isBiValidEnd(endNodeRow + 1, endNodeCol)) {
                if (board[endNodeRow][endNodeCol + 1]==board[endNodeRow][endNodeCol]){
                    foundEndnode=true;
                    break;
                }
                endRectsListTemp.add(rects[endNodeRow + 1][endNodeCol]);
                board[endNodeRow+1][endNodeCol] = seconds;
            }
            if (isBiValidStart(startNodeRow, startNodeCol - 1)) {
                if (board[startNodeRow][startNodeCol - 1]==board[startNodeRow][startNodeCol]){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow][startNodeCol - 1]);
                board[startNodeRow][startNodeCol-1] = seconds;
            }
            if (isBiValidEnd(endNodeRow, endNodeCol - 1)) {
                if (board[endNodeRow][endNodeCol - 1]==board[endNodeRow][endNodeCol]){
                    foundEndnode=true;
                    break;
                }
                endRectsListTemp.add(rects[endNodeRow][endNodeCol - 1]);
                board[endNodeRow][endNodeCol-1] = seconds;
            }
        }
        for (int i=0;i<rectsListTemp.size();i++){
            if(!rectsList.contains(rectsListTemp.get(i))){
                rectsList.add(rectsListTemp.get(i));
                temp.add(rectsListTemp.get(i));
            }
        }
        groupList.add(copy(temp));
        temp.clear();
        for (int i=0;i<endRectsListTemp.size();i++){
            if(!endRectsList.contains(endRectsListTemp.get(i))){
                endRectsList.add(endRectsListTemp.get(i));
                temp.add(endRectsListTemp.get(i));
            }
        }
        groupList.add(copy(temp));
        temp.clear();
        sizes.add(rectsList.size() + endRectsList.size());
        int thing=0;
        while(!foundEndnode) {
            thing++;
            seconds++;
            for (int r = 0; r < rects.length && !foundEndnode; r++) {
                for (int c = 0; c < rects[r].length; c++) {
                    if (rectsList.contains(rects[r][c]) && !foundEndnode) {
                        if (isBiValidStart(r - 1, c)) {
                            if (endRectsListTemp.contains(rects[r-1][c])){
                                foundEndnode=true;
                                foundCol=c;
                                foundRow=r;
                                foundRow2 = r-1;
                                foundCol2 = c;
                                rectsListTemp.add(rects[r - 1][c]);
                            }
                            else {
                                rectsListTemp.add(rects[r - 1][c]);
                                board[r - 1][c] = seconds;
                            }
                        }
                        if (isBiValidStart(r, c + 1) && !foundEndnode) {
                            if (endRectsListTemp.contains(rects[r][c+1])){
                                foundCol=c;
                                foundRow=r;
                                foundEndnode=true;
                                foundRow2 = r;
                                foundCol2 = c+1;
                                rectsListTemp.add(rects[r][c + 1]);
                            }
                            else {
                                rectsListTemp.add(rects[r][c + 1]);
                                board[r][c + 1] = seconds;
                            }
                        }
                        if (isBiValidStart(r + 1, c) && !foundEndnode) {
                            if (endRectsListTemp.contains(rects[r+1][c])){
                                foundCol=c;
                                foundRow=r;
                                foundEndnode=true;
                                foundRow2 = r+1;
                                foundCol2 = c;
                                rectsListTemp.add(rects[r + 1][c]);
                            }
                            else {
                                rectsListTemp.add(rects[r + 1][c]);
                                board[r + 1][c] = seconds;
                            }
                        }
                        if (isBiValidStart(r, c - 1) && !foundEndnode) {
                            if (endRectsListTemp.contains(rects[r][c-1])){//board[r][c]==board[r][c-1]
                                foundCol=c;
                                foundRow=r;
                                foundEndnode=true;
                                foundRow2 = r;
                                foundCol2 = c-1;
                                rectsListTemp.add(rects[r][c - 1]);
                            }
                            else {
                                rectsListTemp.add(rects[r][c - 1]);
                                board[r][c - 1] = seconds;
                            }
                        }

                    }
                    if (endRectsList.contains(rects[r][c]) && !foundEndnode){
                        if (isBiValidEnd(r - 1, c)) {
                            if (rectsListTemp.contains(rects[r-1][c])){
                                foundCol2=c;
                                foundRow2=r;
                                foundEndnode=true;
                                foundRow = r-1;
                                foundCol = c;
                                endRectsListTemp.add(rects[r - 1][c]);
                            }
                            else {
                                endRectsListTemp.add(rects[r - 1][c]);
                                board[r - 1][c] = seconds;
                            }
                        }
                        if (isBiValidEnd(r, c - 1)  && !foundEndnode) {
                            if (rectsListTemp.contains(rects[r][c-1])){
                                foundCol2=c;
                                foundRow2=r;
                                foundEndnode=true;
                                foundRow = r;
                                foundCol = c-1;
                                endRectsListTemp.add(rects[r][c - 1]);
                            }
                            else {
                                endRectsListTemp.add(rects[r][c - 1]);
                                board[r][c - 1] = seconds;
                            }
                        }
                        if (isBiValidEnd(r + 1, c)  && !foundEndnode) {
                            if (rectsListTemp.contains(rects[r+1][c])){
                                foundCol2=c;
                                foundRow2=r;
                                foundEndnode=true;
                                foundRow = r+1;
                                foundCol = c;
                                endRectsListTemp.add(rects[r + 1][c]);
                            }
                            else {
                                endRectsListTemp.add(rects[r + 1][c]);
                                board[r + 1][c] = seconds;
                            }
                        }

                        if (isBiValidEnd(r, c + 1) && !foundEndnode) {
                            if (rectsListTemp.contains(rects[r][c+1])){
                                foundCol2=c;
                                foundRow2=r;
                                foundEndnode=true;
                                foundRow = r;
                                foundCol = c+1;
                                endRectsListTemp.add(rects[r][c + 1]);
                            }
                            else {
                                endRectsListTemp.add(rects[r][c + 1]);
                                board[r][c + 1] = seconds;
                            }
                        }
                    }

                }
            }
            for (int i=0;i<rectsListTemp.size();i++){
                if(!rectsList.contains(rectsListTemp.get(i))){
                    rectsList.add(rectsListTemp.get(i));
                    temp.add(rectsListTemp.get(i));
                }
            }
            groupList.add(copy(temp));
            temp.clear();
            for (int i=0;i<endRectsListTemp.size();i++){
                if(!endRectsList.contains(endRectsListTemp.get(i))){
                    endRectsList.add(endRectsListTemp.get(i));
                    temp.add(endRectsListTemp.get(i));
                }
            }
            groupList.add(copy(temp));
            temp.clear();
            sizes.add(rectsList.size() + endRectsList.size());
            if (sizes.size()>2 && sizes.get(sizes.size()-1).equals(sizes.get(sizes.size()-2)) && !foundEndnode){
                rectsList.add(null);
                break;
            }

        }

        if (foundEndnode) {
            pathList.add(rects[foundRow][foundCol]);
            search(foundRow, foundCol, startNodeRow, startNodeCol, board[foundRow][foundCol], board, rectsList);
            pathList.add(rects[foundRow2][foundCol2]);
            search(foundRow2, foundCol2, endNodeRow, endNodeCol, board[foundRow2][foundCol2], board, endRectsList);
        }
        thing =-1;
        for (int i=0;i<pathList.size();i++){
            if (pathList.get(i)==rects[foundRow2][foundCol2]){
                thing = i;
                break;
            }
        }
        for (int i=thing;i<pathList.size();i++){
            temp.add(pathList.remove(i));
            i--;
        }
        thing =0;
        if (pathList.size()>temp.size()) {
            for (int i = 1; i < pathList.size(); i += 2) {
                pathList.add(i, temp.get(thing));
                thing++;
            }
        }
        else {
            for (int i = 1; i < temp.size(); i += 2) {
                temp.add(i, pathList.get(thing));
                thing++;
            }
            pathList = new ArrayList<>(temp);
        }
        final boolean found = foundEndnode;
        seconds=0;
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                if (finished){
                    if (seconds<0){
                        System.out.println("done");
                        finished=false;
                        ses.shutdown();
                        return;
                    }
                    pathList.get(seconds).setFill(pathColor);
                    seconds--;
                }
                else {
                    if (seconds == groupList.size()) {
                        finished = true;
                        seconds=pathList.size()-1;
                    }
                    else {
                        for (int i = 0; i < groupList.get(seconds).size(); i++) {
                            groupList.get(seconds).get(i).setFill(fillColor);
                        }
                        seconds++;
                    }
                }
            }
            public void bruhMoment(){
                Stage stage=new Stage();
                //stage.initModality(Modality.APPLICATION_MODAL);
                Pane pane = new Pane();
                Text text= new Text(80, 100,"bro, maze no bueno");
                text.setFont(new Font(16));
                pane.getChildren().add(text);
                Scene scene = new Scene(pane, 300, 200);
                stage.setScene(scene);
                stage.showAndWait();
            }
        }, 0, 1001-(long)slider.getValue(), TimeUnit.MILLISECONDS);
    }
    public ArrayList<Rect> copy(ArrayList<Rect> list){
        return new ArrayList<>(list);
    }
    public void dijkstraAlgoth(){
        ArrayList<Rect> closed = new ArrayList<>();
        pathList.clear();
        finished = false;
        int[][] board = new int[row][col];
        int endNodeRow=0;
        int endNodeCol=0;
        int startNodeRow=0;
        int startNodeCol=0;
        sizes.clear();
        rectsList.clear();
        seconds=0;
        ArrayList<Rect> temp = new ArrayList<>();
        boolean foundEndnode = false;
        for (int r=0;r<rects.length;r++){
            for (int c=0;c<rects[r].length;c++){
                if (rects[r][c].getOnColor()==startColor && rects[r][c].getOffColor()==startColor){
                    startNodeRow=r;
                    startNodeCol=c;
                    rectsList.add(rects[r][c]);
                    rects[r][c].setNum(0);
                }
                if (rects[r][c].getFill()!=borderColor && rects[r][c].getFill()!=startColor && rects[r][c].getFill()!=endColor){
                    rects[r][c].setFill(backgroundColor);
                }
            }
        }
        int r;
        int c;
        //System.out.println(startNodeRow +" "+startNodeCol);
        int thing =0;
        while (!foundEndnode){
            thing++;
            seconds++;
            for (int i=0;i<rectsList.size();i++){
                if(!closed.contains(rectsList.get(i))) {
                    r = rectsList.get(i).getR();
                    c = rectsList.get(i).getC();
                    if (isValid(r - 1, c)) {
                        if (rects[r - 1][c].getOnColor() == endColor && rects[r - 1][c].getOffColor() == endColor) {
                            foundEndnode = true;
                            endNodeRow = r - 1;
                            endNodeCol = c;
                            rects[r - 1][c].setNum(seconds);
                            break;
                        } else {
                            temp.add(rects[r - 1][c]);
                            rects[r - 1][c].setNum(seconds);
                        }
                    }
                    if (isValid(r, c + 1)) {
                        if (rects[r][c + 1].getOnColor() == endColor && rects[r][c + 1].getOffColor() == endColor) {
                            foundEndnode = true;
                            endNodeRow = r;
                            endNodeCol = c + 1;
                            rects[r][c + 1].setNum(seconds);
                            break;
                        } else {
                            temp.add(rects[r][c + 1]);
                            rects[r][c + 1].setNum(seconds);
                        }
                    }
                    if (isValid(r + 1, c)) {
                        if (rects[r + 1][c].getOnColor() == endColor && rects[r + 1][c].getOffColor() == endColor) {
                            foundEndnode = true;
                            endNodeRow = r + 1;
                            endNodeCol = c;
                            rects[r + 1][c].setNum(seconds);
                            break;
                        } else {
                            temp.add(rects[r + 1][c]);
                            rects[r + 1][c].setNum(seconds);
                        }
                    }
                    if (isValid(r, c - 1)) {
                        if (rects[r][c - 1].getOnColor() == endColor && rects[r][c - 1].getOffColor() == endColor) {
                            foundEndnode = true;
                            endNodeRow = r;
                            endNodeCol = c - 1;
                            rects[r][c - 1].setNum(seconds);
                            break;
                        } else {
                            temp.add(rects[r][c - 1]);
                            rects[r][c - 1].setNum(seconds);
                        }
                    }
                    closed.add(rects[r][c]);
                }
            }
            for (int z=0;z<temp.size();z++){
                rectsList.add(temp.get(z));
            }
            temp.clear();
        }

        for (int i=0;i<rects.length;i++){
            for (int j=0;j<rects[i].length;j++){
                if (rectsList.contains(rects[i][j])){
                    board[i][j] = rects[i][j].getNum();
                }
            }
        }
        //print(board);
        search(rects[endNodeRow][endNodeCol], rects[startNodeRow][startNodeCol]);
        rectsList.remove(0);
        seconds=0;
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (seconds==rectsList.size()){
                    if (rectsList.get(seconds-1)==null){
                        System.out.println("bro, maze no bueno");
                        ses.shutdown();
                        return;
                    }
                    finished=true;
                    seconds = pathList.size()-1;
                }
                if (finished){
                    if (seconds<0){
                        System.out.println("done");
                        ses.shutdown();
                        return;
                    }
                    if (pathList.get(seconds).getFill()!=endColor) {
                        pathList.get(seconds).setFill(pathColor);
                    }
                    seconds--;
                }
                else {
                    rectsList.get(seconds).setFill(fillColor);
                    seconds++;
                }
            }
        }, 0, 1001-(long)slider.getValue(), TimeUnit.MILLISECONDS);



    }
    public void dijkstraAlgorithm(){
        ArrayList<Rect> open = new ArrayList<>();
        pathList.clear();
        finished = false;
        int[][] board = new int[row][col];
        int endNodeRow=0;
        int endNodeCol=0;
        int startNodeRow=0;
        int startNodeCol=0;
        sizes.clear();
        rectsList.clear();
        seconds=1;
        boolean foundEndnode = false;
        ArrayList<Rect> rectsListTemp = new ArrayList<>();
        for (int r=0;r<rects.length;r++){
            for (int c=0;c<rects[r].length;c++){
                if (rects[r][c].getOnColor()==startColor && rects[r][c].getOffColor()==startColor){
                    startNodeRow=r;
                    startNodeCol=c;

                }
                if (rects[r][c].getFill()!=borderColor && rects[r][c].getFill()!=startColor && rects[r][c].getFill()!=endColor){
                    rects[r][c].setFill(backgroundColor);
                }
            }
        }
        for (int i=0;i<1;i++) {
            if (isValid(startNodeRow - 1, startNodeCol)) {
                if (rects[startNodeRow - 1][startNodeCol].getFill()==endColor){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow - 1][startNodeCol]);
                board[startNodeRow-1][startNodeCol] = seconds;
            }
            if (isValid(startNodeRow, startNodeCol + 1)) {
                if (rects[startNodeRow][startNodeCol+1].getFill()==endColor){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow][startNodeCol + 1]);
                board[startNodeRow][startNodeCol+1] = seconds;
            }
            if (isValid(startNodeRow + 1, startNodeCol)) {
                if (rects[startNodeRow + 1][startNodeCol].getFill()==endColor){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow + 1][startNodeCol]);
                board[startNodeRow+1][startNodeCol] = seconds;
            }
            if (isValid(startNodeRow, startNodeCol - 1)) {
                if (rects[startNodeRow][startNodeCol - 1].getFill()==endColor){
                    foundEndnode=true;
                    break;
                }
                rectsListTemp.add(rects[startNodeRow][startNodeCol - 1]);
                board[startNodeRow][startNodeCol-1] = seconds;
            }

        }
        if (!foundEndnode) {
            rectsList = new ArrayList<>(rectsListTemp);
            sizes.add(rectsList.size());
        }
        if (sizes.contains(0)){
            rectsList.add(null);
            foundEndnode=true;
        }
        while(!foundEndnode) {
            seconds++;
            for (int r = 0; r < rects.length && !foundEndnode; r++) {
                for (int c = 0; c < rects[r].length; c++) {
                    if (rectsList.contains(rects[r][c])) {
                        if (isValid(r - 1, c)) {
                            if (rects[r-1][c].getFill()==endColor){
                                foundEndnode=true;
                                endNodeRow = r-1;
                                endNodeCol = c;
                                break;
                            }
                            rectsListTemp.add(rects[r - 1][c]);
                            board[r-1][c] = seconds;
                        }
                        if (isValid(r, c + 1)) {
                            if (rects[r][c+1].getFill()==endColor){
                                foundEndnode=true;
                                endNodeRow = r;
                                endNodeCol = c+1;
                                break;
                            }
                            rectsListTemp.add(rects[r][c+1]);
                            board[r][c+1] = seconds;
                        }
                        if (isValid(r + 1, c)) {
                            if (rects[r+1][c].getFill()==endColor){
                                foundEndnode=true;
                                endNodeRow = r+1;
                                endNodeCol = c;
                                break;
                            }
                            rectsListTemp.add(rects[r + 1][c]);
                            board[r+1][c] = seconds;
                        }
                        if (isValid(r, c - 1)) {
                            if (rects[r][c-1].getFill()==endColor){
                                foundEndnode=true;
                                endNodeRow = r;
                                endNodeCol = c-1;
                                break;
                            }
                            rectsListTemp.add(rects[r][c - 1]);
                            board[r][c-1] = seconds;
                        }

                    }

                }
            }
            for (int i=0;i<rectsListTemp.size();i++){
                if(!rectsList.contains(rectsListTemp.get(i))){
                    rectsList.add(rectsListTemp.get(i));
                }
            }
            sizes.add(rectsList.size());
            if (sizes.get(sizes.size()-1).equals(sizes.get(sizes.size()-2)) && !foundEndnode){
                rectsList.add(null);
                break;
            }
        }
        search(endNodeRow, endNodeCol, startNodeRow, startNodeCol, seconds, board);
        System.out.println(pathList);
        seconds=0;
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (seconds==rectsList.size()-1){
                    if (rectsList.get(seconds)==null){
                        System.out.println("bro, maze no bueno");
                        ses.shutdown();
                        return;
                    }
                    finished=true;
                    seconds = pathList.size()-1;
                }
                if (finished){
                    if (seconds<0){
                        System.out.println("done");
                        ses.shutdown();
                        return;
                    }
                    if (pathList.get(seconds).getFill()!=endColor) {
                        pathList.get(seconds).setFill(pathColor);
                    }
                    seconds--;
                }
                else {
                    rectsList.get(seconds).setFill(fillColor);
                    seconds++;
                }
            }
            public void bruhMoment(){
                Stage stage=new Stage();
                //stage.initModality(Modality.APPLICATION_MODAL);
                Pane pane = new Pane();
                Text text= new Text(80, 100,"bro, maze no bueno");
                text.setFont(new Font(16));
                pane.getChildren().add(text);
                Scene scene = new Scene(pane, 300, 200);
                stage.setScene(scene);
                stage.showAndWait();
            }
        }, 0, 1001-(long)slider.getValue(), TimeUnit.MILLISECONDS);
    }
    public void aStarAlgorithm(){
        ArrayList<Rect> open = new ArrayList<>();
        ArrayList<Rect> closed = new ArrayList<>();
        pathList = new ArrayList<>();
        finished = false;
        int[][] board = new int[row][col];
        int endNodeRow=0;
        int endNodeCol=0;
        int startNodeRow=0;
        int startNodeCol=0;
        sizes.clear();
        rectsList.clear();
        seconds=1;
        boolean foundEndnode = false;
        ArrayList<Rect> rectsListTemp = new ArrayList<>();
        for (int r=0;r<rects.length;r++){
            for (int c=0;c<rects[r].length;c++){
                if (rects[r][c].getFill()==startColor){
                    startNodeRow=r;
                    startNodeCol=c;
                    rects[r][c].setG(0);
                    rects[r][c].setH(distance(rects[r][c].getR(), rects[r][c].getC(), endNodeRow, endNodeCol));
                    rects[r][c].setNum(rects[r][c].getG()+rects[r][c].getH());
                    open.add(rects[r][c]);
                }
                if (rects[r][c].getFill()==endColor){
                    endNodeRow=r;
                    endNodeCol=c;
                }
                if (rects[r][c].getFill()!=borderColor && rects[r][c].getFill()!=startColor && rects[r][c].getFill()!=endColor){
                    rects[r][c].setFill(backgroundColor);
                }
            }
        }
        int min;
        Rect current=null;
        int thing =0;
        while (!foundEndnode){
            rectsListTemp.clear();
            min = Integer.MAX_VALUE;
            /*
            for (int i=0;i<open.size();i++){
                open.get(i).setG(distance(open.get(i).getR(), open.get(i).getC(), startNodeRow, startNodeCol));
                open.get(i).setH(distance(open.get(i).getR(), open.get(i).getC(), endNodeRow, endNodeCol));
                open.get(i).setNum(open.get(i).getH()+open.get(i).getG());
                board[open.get(i).getR()][open.get(i).getC()] = open.get(i).getH()+open.get(i).getG();
            }

             */
            for (int i=0;i<open.size();i++){
                if(open.get(i).getNum()<=min){
                    min = open.get(i).getNum();
                }
            }
            for (int i=0;i<open.size();i++){
                if (open.get(i).getNum()==min){
                    rectsListTemp.add(open.get(i));
                    //break;

                }
            }
            min = Integer.MAX_VALUE;
            for (int i=0;i<rectsListTemp.size();i++){

                if(rectsListTemp.get(i).getG()<=min){
                    min = rectsListTemp.get(i).getG();
                }
            }
            for (int i=0;i<rectsListTemp.size();i++){
                if (rectsListTemp.get(i).getG()!=min){
                    rectsListTemp.remove(i);
                    i--;
                }
            }
            for (int z=0;z<rectsListTemp.size();z++) {
                current = new Rect(rectsListTemp.get(z).getOnColor(), rectsListTemp.get(z).getOnColor(), rectsListTemp.get(z).getNum(), rectsListTemp.get(z).getScene(), rectsListTemp.get(z).getXpos(), rectsListTemp.get(z).getYpos(), squareLength);
                current.setH(rectsListTemp.get(z).getH());
                current.setG(rectsListTemp.get(z).getG());
                for (int i = 0; i < open.size(); i++) {
                    if (open.get(i).getC() == current.getC() && open.get(i).getR() == current.getR()) {
                        open.remove(open.get(i));
                        i--;
                    }
                }

                closed.add(current);
                board[current.getR()][current.getC()] = rects[current.getR()][current.getC()].getNum();
                /*
                if (current.getR() == endNodeRow && current.getC() == endNodeCol) {
                    searchAStar(current.getR(), current.getC());
                    pathList.remove(0);
                    foundEndnode = true;
                    break;
                }
                 */
                if (isValid(current.getR(), current.getC() + 1) && !contains(closed, rects[current.getR()][current.getC() + 1])) {
                    if (current.getR() == endNodeRow && current.getC()+1== endNodeCol) {
                        rectsList.add(rects[current.getR()][current.getC() + 1]);
                        searchAStar(current.getR(), current.getC());
                        foundEndnode = true;
                        break;
                    }
                    if (current.getG()+1 < rects[current.getR()][current.getC() + 1].getG() || !open.contains(rects[current.getR()][current.getC() + 1])) {
                        rects[current.getR()][current.getC() + 1].setG(current.getG()+1);
                        rects[current.getR()][current.getC() + 1].setH(distance(current.getR(), current.getC() + 1, endNodeRow, endNodeCol));
                        rects[current.getR()][current.getC() + 1].setNum(rects[current.getR()][current.getC() + 1].getG() + rects[current.getR()][current.getC() + 1].getH());
                        rects[current.getR()][current.getC() + 1].setParent(current);
                        if (!open.contains(rects[current.getR()][current.getC() + 1])) {
                            rectsList.add(rects[current.getR()][current.getC() + 1]);
                            open.add(rects[current.getR()][current.getC() + 1]);
                            rects[current.getR()][current.getC() + 1].setParent(current);
                        }
                    }

                }
                if (isValid(current.getR() + 1, current.getC()) && !contains(closed, rects[current.getR() + 1][current.getC()])) {
                    if (current.getR()+1 == endNodeRow && current.getC() == endNodeCol) {
                        rectsList.add(rects[current.getR() + 1][current.getC()]);
                        searchAStar(current.getR(), current.getC());
                        foundEndnode = true;
                        break;
                    }
                    if (current.getG()+1 < rects[current.getR() + 1][current.getC()].getG() || !open.contains(rects[current.getR() + 1][current.getC()])) {
                        rects[current.getR() + 1][current.getC()].setG(current.getG()+1);
                        rects[current.getR() + 1][current.getC()].setH(distance(current.getR() + 1, current.getC(), endNodeRow, endNodeCol));
                        rects[current.getR() + 1][current.getC()].setNum(rects[current.getR() + 1][current.getC()].getH() + rects[current.getR() + 1][current.getC()].getG());
                        rects[current.getR() + 1][current.getC()].setParent(current);
                        if (!open.contains(rects[current.getR() + 1][current.getC()])) {
                            rectsList.add(rects[current.getR() + 1][current.getC()]);
                            open.add(rects[current.getR() + 1][current.getC()]);
                            rects[current.getR() + 1][current.getC()].setParent(current);
                        }
                    }

                }
                if (isValid(current.getR(), current.getC() - 1) && !contains(closed, rects[current.getR()][current.getC() - 1])) {
                    if (current.getR() == endNodeRow && current.getC()-1 == endNodeCol) {
                        rectsList.add(rects[current.getR()][current.getC() - 1]);
                        searchAStar(current.getR(), current.getC());
                        foundEndnode = true;
                        break;
                    }
                    if (current.getG()+1 < rects[current.getR()][current.getC() - 1].getG() || !open.contains(rects[current.getR()][current.getC() - 1])) {
                        rects[current.getR()][current.getC() - 1].setG(current.getG()+1);
                        rects[current.getR()][current.getC() - 1].setH(distance(current.getR(), current.getC() - 1,endNodeRow, endNodeCol));
                        rects[current.getR()][current.getC() - 1].setNum(rects[current.getR()][current.getC() - 1].getG()+rects[current.getR()][current.getC() - 1].getH());
                        rects[current.getR()][current.getC() - 1].setParent(current);
                        if (!open.contains(rects[current.getR()][current.getC() - 1])) {
                            rectsList.add(rects[current.getR()][current.getC() - 1]);
                            open.add(rects[current.getR()][current.getC() - 1]);
                            rects[current.getR()][current.getC() - 1].setParent(current);
                        }
                    }

                }
                if (isValid(current.getR() - 1, current.getC()) && !contains(closed, rects[current.getR() - 1][current.getC()])) {
                    if (current.getR()-1 == endNodeRow && current.getC() == endNodeCol) {
                        rectsList.add(rects[current.getR() - 1][current.getC()]);
                        searchAStar(current.getR(), current.getC());
                        foundEndnode = true;
                        break;
                    }
                    if (current.getG()+1 < rects[current.getR() - 1][current.getC()].getG() || !open.contains(rects[current.getR() - 1][current.getC()])) {
                        rects[current.getR() - 1][current.getC()].setG(current.getG()+1);
                        rects[current.getR() - 1][current.getC()].setH(distance(current.getR() - 1, current.getC(), endNodeRow, endNodeCol));
                        rects[current.getR() - 1][current.getC()].setNum(rects[current.getR() - 1][current.getC()].getG()+rects[current.getR() - 1][current.getC()].getH());
                        rects[current.getR() - 1][current.getC()].setParent(current);
                        if (!open.contains(rects[current.getR() - 1][current.getC()])) {
                            rectsList.add(rects[current.getR() - 1][current.getC()]);
                            open.add(rects[current.getR() - 1][current.getC()]);
                            rects[current.getR() - 1][current.getC()].setParent(current);
                        }
                    }

                }
            }
            sizes.add(rectsList.size());
            /*
            if (!foundEndnode && sizes.size()>3 && sizes.get(sizes.size()-1) == sizes.get(sizes.size()-2) && sizes.get(sizes.size()-2)==sizes.get(sizes.size()-3)){
                rectsList.add(null);
                break;
            }

             */
            if (triple(sizes)){
                rectsList.add(null);
                break;
            }
            thing++;




        }

        seconds=0;
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (finished){
                    if (seconds<0){
                        System.out.println("done");
                        finished=false;
                        ses.shutdown();
                        return;
                    }
                    pathList.get(seconds).setFill(pathColor);
                    seconds--;
                }
                else {
                    rectsList.get(seconds).setFill(fillColor);
                    seconds++;
                }
                if (seconds==rectsList.size()-1){
                    if (rectsList.get(seconds)==null){
                        System.out.println("bro, maze no bueno");
                        ses.shutdown();
                        return;
                    }

                    finished=true;
                    seconds = pathList.size()-1;
                }
            }
        }, 0, 1001-(long)slider.getValue(), TimeUnit.MILLISECONDS);
        //print(board);

    }
    public static boolean duplicate(ArrayList<Integer> arr){
        int temp;
        for (int i=0;i<arr.size();i++){
            temp=arr.get(i);
            for (int z=i+1;z<arr.size();z++){
                if (arr.get(z)==temp){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean triple(ArrayList<Integer> arr){
        int count;
        int temp;
        for (int i=0;i<arr.size();i++){
            count=0;
            temp=arr.get(i);
            for (int z=i+1;z<arr.size();z++){
                if (arr.get(z)==temp){
                    count++;
                }
                if (count==50){
                    return true;
                }
            }
        }
        return false;
    }


    public void activateBorders(){
        scene.setOnMouseDragReleased(e->{
            for (int r=0;r<rects.length;r++) {
                for (int c = 0; c < rects[r].length; c++) {
                    rects[r][c].setColor();
                }
            }
        });
        scene.setOnMouseDragged(e-> {
            for (int r=0;r<rects.length;r++) {
                for (int c = 0; c < rects[r].length; c++) {
                    rects[r][c].setToNothing();
                }
            }
            boolean withinBounds;
            for (int r=0;r<rects.length;r++){
                for (int c=0;c<rects[r].length;c++) {
                    withinBounds = e.getSceneX() >= rects[r][c].getX() && e.getSceneX() <= rects[r][c].getX() + squareLength && e.getSceneY() >= rects[r][c].getY() && e.getSceneY() <= rects[r][c].getY() + squareLength;
                    if (!e.isSecondaryButtonDown() && withinBounds) {
                        if (rects[r][c].getFill()==endColor) {
                            rects[r][c].setFill(borderColor);
                            rects[r][c].setOn(true);
                            rects[r][c].setOffColor(backgroundColor);
                            rects[r][c].setOnColor(borderColor);
                            //pane.getChildren().add(rects[r][c]);
                            hasEndNode= false;
                        }
                        if (rects[r][c].getFill()==startColor) {
                            rects[r][c].setFill(borderColor);
                            rects[r][c].setOn(true);
                            rects[r][c].setOffColor(backgroundColor);
                            rects[r][c].setOnColor(borderColor);
                            //pane.getChildren().add(rects[r][c]);
                            hasStartNode = false;
                        }
                        rects[r][c].setFill(borderColor);
                        rects[r][c].setOn(true);
                    }

                }
            }
            for (int r=0;r<rects.length;r++){
                for (int c=0;c<rects[r].length;c++) {
                    withinBounds = e.getSceneX() >= rects[r][c].getX() && e.getSceneX() <= rects[r][c].getX() + squareLength && e.getSceneY() >= rects[r][c].getY() && e.getSceneY() <= rects[r][c].getY() + squareLength;
                    if (e.isSecondaryButtonDown() && withinBounds) {
                        if (rects[r][c].getFill()==endColor) {
                            rects[r][c].setFill(backgroundColor);
                            rects[r][c].setOffColor(backgroundColor);
                            rects[r][c].setOnColor(borderColor);
                            rects[r][c].setOn(false);
                            //pane.getChildren().add(rects[r][c]);
                            hasEndNode=false;
                        }
                        if (rects[r][c].getFill()==startColor) {
                            rects[r][c].setFill(backgroundColor);
                            rects[r][c].setOn(false);
                            rects[r][c].setOffColor(backgroundColor);
                            rects[r][c].setOnColor(borderColor);
                            // pane.getChildren().add(rects[r][c]);
                            hasStartNode=false;
                        }
                        rects[r][c].setFill(backgroundColor);
                        rects[r][c].setOn(false);
                    }

                }
            }
        });
        scene.setOnMousePressed(e->{
            for (int r=0;r<rects.length;r++){
                for (int c=0;c<rects[r].length;c++){
                    if (rects[r][c].getFill()!=endColor&& rects[r][c].getFill()!=startColor) {
                        rects[r][c].setColor();
                    }
                }
            }
        });
    }
    public void pickStartNode(){
        scene.setOnMouseDragged(e-> System.out.print(""));
        scene.setOnMousePressed(e -> {
            boolean withinBounds;
            for (int r = 0; r < rects.length; r++) {
                for (int c=0;c<rects[r].length;c++) {
                    withinBounds = e.getSceneX() >= rects[r][c].getX() && e.getSceneX() <= rects[r][c].getX() + squareLength && e.getSceneY() >= rects[r][c].getY() && e.getSceneY() <= rects[r][c].getY() + squareLength;
                    if (rects[r][c].getFill()!=endColor && !hasStartNode && !e.isSecondaryButtonDown() && withinBounds) {
                        rects[r][c] = new Rect(startColor, startColor, rects[r][c].getNum(), rects[r][c].getScene(), rects[r][c].getXpos(), rects[r][c].getYpos(), squareLength);
                        pane.getChildren().add(rects[r][c]);
                        hasStartNode = true;
                    }
                    else if (rects[r][c].getFill()==startColor && hasStartNode && e.isSecondaryButtonDown() && withinBounds) {
                        rects[r][c] = new Rect(borderColor, backgroundColor, rects[r][c].getNum(), rects[r][c].getScene(), rects[r][c].getXpos(), rects[r][c].getYpos(), squareLength);
                        rects[r][c].setColor();
                        pane.getChildren().add(rects[r][c]);
                        hasStartNode = false;
                    }
                }
            }
        });
    }
    public void pickEndNode(){
        scene.setOnMouseDragged(e-> System.out.print(""));
        scene.setOnMousePressed(e -> {
            boolean withinBounds;
            for (int r = 0; r < rects.length; r++) {
                for (int c=0;c<rects[r].length;c++) {
                    withinBounds = e.getSceneX() >= rects[r][c].getX() && e.getSceneX() <= rects[r][c].getX() + squareLength && e.getSceneY() >= rects[r][c].getY() && e.getSceneY() <= rects[r][c].getY() + squareLength;
                    if (rects[r][c].getFill()!=startColor&& !hasEndNode && !e.isSecondaryButtonDown() && withinBounds) {
                        rects[r][c] = new Rect(endColor, endColor, rects[r][c].getNum(), rects[r][c].getScene(), rects[r][c].getXpos(), rects[r][c].getYpos(), squareLength);
                        pane.getChildren().add(rects[r][c]);
                        hasEndNode = true;
                    }
                    else if (rects[r][c].getFill()==endColor && hasEndNode && e.isSecondaryButtonDown() && withinBounds) {
                        rects[r][c] = new Rect(borderColor, backgroundColor, rects[r][c].getNum(), rects[r][c].getScene(), rects[r][c].getXpos(), rects[r][c].getYpos(), squareLength);
                        rects[r][c].setColor();
                        pane.getChildren().add(rects[r][c]);
                        hasEndNode = false;
                    }
                }
            }
        });


    }
    public void print(int[][] arr){
        for (int[] r: arr){
            for (int c: r){
                System.out.print(c+" ");
            }
            System.out.println();
        }
    }

    public int getSquareLength() {
        return squareLength;
    }
    public Stage getStage() {
        return stage;
    }
}
