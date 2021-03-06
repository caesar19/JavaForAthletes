package client_server;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import src.Const;
import src.ObjectsTable;

public class HomeController extends Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<ObjectsTable> Objects_table;

    @FXML
    private TableColumn<ObjectsTable, Integer> idColumn;

    @FXML
    private TableColumn<ObjectsTable, String> keyColumn;

    @FXML
    private TableColumn<ObjectsTable, String> nameColumn;

    @FXML
    private TableColumn<ObjectsTable, String> creatorColumn;

    @FXML
    private Button remove_btn;

    @FXML
    private Button insert_btn;

    @FXML
    private TextField Edit_name;

    @FXML
    private TextField Edit_key;

    @FXML
    private Button exit_btn;

    @FXML
    private Label hello_label;

    @FXML
    private MenuBar lg_menu;

    @FXML
    private Menu rus_menu;

    @FXML
    private Menu eng_menu;

    @FXML
    private Menu est_menu;

    @FXML
    private Menu swe_menu;

    @FXML
    private Button grafBtn;


    static ObservableList<ObjectsTable> oblist = FXCollections.observableArrayList();

    int last_id = 0;

    Connection dbconnection;

    public Connection getDbconnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        dbconnection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/base", "us", "qwerty");
        return dbconnection;
    }

    @FXML
    void initialize() {

        hello_label.setText("Привет, " + current_user_creator);


        try {
            ResultSet rs = getDbconnection().createStatement().executeQuery("SELECT * FROM " + Const.ANIMAL_TABLE);
            while (rs.next()) {
                oblist.add(new ObjectsTable(rs.getInt("id_objects"), rs.getString("key"),
                        rs.getString("name"), rs.getString("creator")));
                last_id = rs.getInt("id_objects");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creatorColumn.setCellValueFactory(new PropertyValueFactory<>("creator"));

        Objects_table.setEditable(true);
        Objects_table.setItems(oblist);


        /*keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        keyColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ObjectsTable, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ObjectsTable, String> event) {
                        event.getTableView().getItems().get(
                                event.getTablePosition().getColumn()
                        ).setKey(event.getNewValue());
                    }
                }
        );
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ObjectsTable, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ObjectsTable, String> event) {
                        event.getTableView().getItems().get(
                                event.getTablePosition().getRow()
                        ).setName(event.getNewValue());
                    }
                }
        );
        */


        insert_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int finalLast_id = last_id;
                String editKey = Edit_key.getText().trim();
                String editName = Edit_name.getText().trim();

                if (!editKey.equals("") && !editName.equals("")) {
                    if ((editName.contains("Страшный зверь") || editName.contains("Неизвестный зверь")) && editKey.contains("Зверь")) {
                        try {
                            ResultSet resultSet = getremoveAnimal(editKey, current_user_creator);
                            ResultSet resultSet1 = getAnimalKey(editKey);
                            int row_counter = 0;
                            int row_counter_key = 0;
                            try {
                                while (resultSet.next()) {
                                    row_counter++;
                                }
                                while (resultSet1.next()) {
                                    row_counter_key++;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (row_counter == 0 & row_counter_key == 0) {
                                oblist.add(new ObjectsTable(finalLast_id + 1, Edit_key.getText(), Edit_name.getText(), current_user_creator));
                                last_id++;
                                signUpAnimal(editKey, editName, current_user_creator);
                                signAss(editKey, current_user_creator, "insert");
                                Edit_name.clear();
                                Edit_key.clear();
                            } else if (row_counter == 1 & row_counter_key == 1) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Так как зверь с таким ключом уже создан вами, произвелась замена его названия");
                                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                                alert.show();

                                for (int i = 0; i < oblist.size(); i++) {
                                    if (editKey.equals(oblist.get(i).getKey())) {
                                        oblist.get(i).setName(editName);
                                        Objects_table.refresh();
                                    }
                                }
                                renameAnimal(editKey, editName);
                                signAss(editKey, current_user_creator, "rename");
                                Edit_name.clear();
                                Edit_key.clear();

                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Этот объект создан другим пользователем, вы не имеете права его изменять");
                                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                                alert.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Имя и ключ должны быть в формате - Пример: Зверь1 Страшный зверь 1");
                        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                        alert.show();
                        Edit_name.clear();
                        Edit_key.clear();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Не оставляйте поля пустыми!");
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                    alert.show();
                }
            }
        });

        exit_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                current_user_creator = null;
                Stage stage = (Stage) exit_btn.getScene().getWindow();
                stage.close();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/sample.fxml"));
                Parent root1 = null;
                try {
                    root1 = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root1));
                stage.show();
            }
        });

        grafBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) grafBtn.getScene().getWindow();
                stage.close();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/graf.fxml"));
                Parent root1 = null;
                try {
                    root1 = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root1));


                Group root = new Group();
                Canvas canvas = new Canvas(500, 300);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                GraphicsContext pc = canvas.getGraphicsContext2D();
                Stop[] stops1;
                Stop[] stops2;
                LinearGradient gradient1;
                LinearGradient gradient2;
                for (int i = 0; i < oblist.size(); i++) {
                    if (oblist.get(i).getName().contains("Страшный зверь")) {

                        gc.beginPath();
                        stops1 = new Stop[]{new Stop(0, Color.RED), new Stop(1, Color.BLACK)};
                        gradient1 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops1);
                        gc.setFill(gradient1);
                        gc.fillOval((i+3)*80, i*50, 40, 40);
                        gc.fill();
                        gc.setStroke(Color.WHITE);
                    }

                    if (oblist.get(i).getName().contains("Неизвестный зверь")) {
                        pc.beginPath();
                        stops2 = new Stop[]{new Stop(0, Color.BLUE), new Stop(1, Color.YELLOW)};
                        gradient2 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops2);
                        pc.setFill(gradient2);
                        pc.fillOval(i*20, (i+4)*40, 40, 40);
                        pc.fill();
                        pc.setStroke(Color.WHITE);
                        //pc.setFont(Font.font("Arial", 10));
                        //pc.fillText("ЗВЕРЮГА", 10, 10);
                    }
                }

                Label label = new Label();
                //label.setText();
                label.setOnMouseEntered(event2 -> {
                    label.setScaleX(1.5);
                    label.setScaleY(1.5);
                    pc.setStroke(Color.BLACK);
                    //Здесь созаем окно с инфой о звере
                });
                label.setOnMouseExited(event2 -> {
                    label.setScaleX(1.5);
                    label.setScaleY(1.5);
                    pc.setFill(Color.RED);
                    //Закрываем окно с инфой о звере
                });
                root.getChildren().add(canvas);
                Scene scene = new Scene(root, 500, 300, Color.GREEN);
                stage.setScene(scene);

                stage.show();
            }
        });

        remove_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String editKey = Edit_key.getText().trim();
                String editName = Edit_name.getText().trim();
                if (!editKey.equals("")) {
                    ResultSet result = getremoveAnimal(editKey, current_user_creator);
                    int row_counter = 0;
                    try {
                        while (result.next()) {
                            row_counter++;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (row_counter >= 1) {
                        for (int i = 0; i < oblist.size(); i++) {
                            if (editKey.equals(oblist.get(i).getKey())) {
                                oblist.remove(i);
                                Objects_table.refresh();
                            }
                        }
                        removeAnimal(editKey, current_user_creator);
                        signAss(editKey, current_user_creator, "remove");
                        Edit_name.clear();
                        Edit_key.clear();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Вы не можете удалить данный объект, т.к. он создан другим пользователем!");
                        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                        alert.show();
                        Edit_name.clear();
                    }

                } else {
                    final char dm = (char) 34;
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Не оставляйте поле " + dm + "key" + dm + " пустым!");
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                    alert.show();
                }
            }
        });

    }


    public ResultSet getremoveAnimal(String key, String user_login) {
        ResultSet resultSet = null;

        String select = "SELECT * FROM " + Const.ANIMAL_TABLE + " WHERE " +
                Const.ANIMAL_KEY + "=? AND " + Const.ANIMAL_CREATOR + "=?";
        try {
            PreparedStatement prSt = getDbconnection().prepareStatement(select);
            prSt.setString(1, key);
            prSt.setString(2, user_login);
            resultSet = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public ResultSet getAnimalKey(String key) {
        ResultSet resultSet = null;

        String select = "SELECT * FROM " + Const.ANIMAL_TABLE + " WHERE " +
                Const.ANIMAL_KEY + "=?";
        try {
            PreparedStatement prSt = getDbconnection().prepareStatement(select);
            prSt.setString(1, key);
            resultSet = prSt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public void signAss(String key, String user, String action) {
        String ass_insert = "INSERT INTO " + Const.ASS_TABLE + "(" +
                Const.ASS_OBJECT_KEY + "," +
                Const.ASS_USERNAME + "," +
                Const.ASS_ACTION + ")" +
                "VALUES(?,?,?)";
        try {
            PreparedStatement prSt1 = getDbconnection().prepareStatement(ass_insert);
            prSt1.setString(1, key);
            prSt1.setString(2, user);
            prSt1.setString(3, action);
            prSt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void signUpAnimal(String key, String name, String user_login) {
        String insert = "INSERT INTO " + Const.ANIMAL_TABLE + "(" +
                Const.ANIMAL_KEY + "," +
                Const.ANIMAL_NAME + "," +
                Const.ANIMAL_CREATOR + ")" +
                "VALUES(?,?,?)";
        try {
            PreparedStatement prSt = getDbconnection().prepareStatement(insert);
            prSt.setString(1, key);
            prSt.setString(2, name);
            prSt.setString(3, user_login);
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void renameAnimal(String key, String name) {
        String update = "UPDATE " + Const.ANIMAL_TABLE + " SET " +
                Const.ANIMAL_NAME + "=?" + " WHERE " +
                Const.ANIMAL_KEY + "=?";
        try {
            PreparedStatement prSt = getDbconnection().prepareStatement(update);
            prSt.setString(1, name);
            prSt.setString(2, key);
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeAnimal(String key, String user_login) {
        String delete = "DELETE FROM " + Const.ANIMAL_TABLE + " WHERE " +
                Const.ANIMAL_KEY + "=? AND " +
                Const.ANIMAL_CREATOR + "=?";
        try {
            PreparedStatement prSt = getDbconnection().prepareStatement(delete);
            prSt.setString(1, key);
            prSt.setString(2, user_login);
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

