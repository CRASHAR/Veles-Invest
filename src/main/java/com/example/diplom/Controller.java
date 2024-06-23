package com.example.diplom;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private VBox myVbox;
    @FXML
    private TableView<Skin> skinsTable;
    @FXML
    private TableColumn<Skin, String> columnName;
    @FXML
    private TableColumn<Skin, Double> columnPurchase;
    @FXML
    private TableColumn<Skin, Double> columnCurrent;
    @FXML
    private TableColumn<Skin, Double> columnSale;
    @FXML
    private TableColumn<Skin, Double> columProfit;
    @FXML
    private TableColumn<Skin, Double> columProfitPercent;
    @FXML
    private TableColumn<Skin, String> columnItemType;
    @FXML
    private TableColumn<Skin, Void> actionColumn;
    @FXML
    private TableColumn<Skin, Double> potentialProfitPercentColumn;
    @FXML
    private TextField textPurchasePrice;
    @FXML
    private Label investedLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private Label profitLabel;
    @FXML
    private Label profitPercentLabel;
    private ObservableList<Skin> portfolio = FXCollections.observableArrayList();
    private ComboBox<String> comboBox = new ComboBox<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ListOfSkins listOfSkins = new ListOfSkins();
        listOfSkins.setListOfSkins();
        comboBox.setEditable(true);
        comboBox.setPromptText("Введите название предмета");
        comboBox.setPrefWidth(390);
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = newValue.toLowerCase();
            comboBox.getItems().clear();
            comboBox.getItems().addAll(FXCollections.observableArrayList(
                            listOfSkins.list)
                    .filtered(s -> s.toLowerCase().contains(enteredText)));
        });

        myVbox.getChildren().add(comboBox);

        //Связываем столбцы с соответствующими свойствами модели данных
        columnName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        columnPurchase.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        columnCurrent.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        columnSale.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        columProfit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        columProfitPercent.setCellValueFactory(new PropertyValueFactory<>("profitPercent"));
        columnItemType.setCellValueFactory(new PropertyValueFactory<>("itemType"));

        //Позволяем редактировать ячейки стобцов
        columnPurchase.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        columnSale.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Добавляем слушателя изменений для ячеек столбца columnSale
        columnSale.setOnEditCommit(event -> {
            // Получаем новое значение из события
            Double newValue = event.getNewValue();
            // Получаем индекс строки, которая была изменена
            int rowIndex = event.getTablePosition().getRow();
            // Получаем объект Skin, соответствующий измененной строке
            skinsTable.getItems().get(rowIndex).setSalePrice(newValue);
            try {
                profitCalculation();
                // Расчет и вывод общих показателей
                calculationOfGeneralparameters();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Добавляем слушателя изменений для ячеек столбца columnPurchase
        columnPurchase.setOnEditCommit(event -> {
            // Получаем новое значение из события
            Double newValue = event.getNewValue();
            // Получаем индекс строки, которая была изменена
            int rowIndex = event.getTablePosition().getRow();
            // Получаем объект Skin, соответствующий измененной строке
            skinsTable.getItems().get(rowIndex).setPurchasePrice(newValue);
            try {
                profitCalculation();
                // Расчет и вывод общих показателей
                calculationOfGeneralparameters();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Расчет и вывод общих показателей
        });

        // Меняем цвет столбца "Доход", "Доход, %", "Потенциал" в зависимости от значения
        setColorColumn(columProfit);
        setColorColumn(columProfitPercent);
        setColorColumn(potentialProfitPercentColumn);

        // Настраиваем столбец "Тип предмета", добавляем в него choiceBox с выбором
        addChoiceBoxes();
        // Столбец с кнопкой удаления для скинов
        addDeleteButtonToTable();
        calculatePotentialProfitPercent();
    }

    @FXML
    protected void addItem() throws IOException {
        if (comboBox.getValue() != null && !comboBox.getValue().trim().isEmpty() && textPurchasePrice.getText() != null && !textPurchasePrice.getText().trim().isEmpty()) {
            Skin skin = new Skin(comboBox.getValue(), Double.parseDouble(textPurchasePrice.getText()));
            skin.setCurrentValue();
            portfolio.add(skin);

            skinsTable.setItems(portfolio);

            // Расчет и вывод общих показателей
            calculationOfGeneralparameters();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Сообщение об ошибке");
            alert.setHeaderText("Ошибка ввода!");
            alert.setContentText("Поля \"Название предмета\" и \"Стоимость\" не могуть быть пустыми");
            // Отображаем диалоговое окно и ждем, пока пользователь его закроет
            alert.showAndWait();
        }
    }

    @FXML
    protected void profitCalculation() throws IOException {
        //Рассчитываем доход и доход в процентах
        if (!portfolio.isEmpty()) {
            for (Skin skin : skinsTable.getItems()) {
                if (skin.getSalePrice() != 0) {
                    double purchasePrice = skin.getPurchasePrice();
                    double salePrice = skin.getSalePrice();
                    double profit = salePrice - purchasePrice;
                    skin.setProfit((double) Math.round(profit * 100) / 100);
                    skin.setProfitPercent((double) Math.round((salePrice - purchasePrice) / purchasePrice * 10000) / 100);
                }
            }
            // После обновления значений вызываем метод refresh(), чтобы обновить отображение таблицы
            skinsTable.refresh();
        }
    }

    // Меняем цвет текста в столбце, на вход подается столбец
    private void setColorColumn(TableColumn<Skin, Double> column) {
        column.setCellFactory(cell -> new TableCell<Skin, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle(""); // Устанавливаем пустой стиль для сброса
                } else {
                    setText(String.valueOf(item)); // Устанавливаем текст ячейки
                    // Устанавливаем цвет текста в зависимости от значения item
                    if (item < 0) {
                        setStyle("-fx-text-fill: red;");
                    } else if (item > 0) {
                        setStyle("-fx-text-fill: #94FF00;");
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                }
            }
        });
    }

    // Добавляем в ячейки столбца "Тип предмета" choiceBox
    private void addChoiceBoxes() {
        columnItemType.setCellFactory(column -> {
            return new TableCell<Skin, String>() {
                private final ChoiceBox<String> choiceBox = new ChoiceBox<>();

                {
                    // Добавляем элементы в ChoiceBox
                    choiceBox.getItems().addAll("Оружие", "Кейс", "Стикер", "Другое");

                    // Слушатель для выбора элемента
                    choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        // Получаем объект Skin, соответствующий этой строке
                        Skin skin = getTableView().getItems().get(getIndex());
                        // Устанавливаем новое значение типа скина
                        skin.setItemType(newValue);
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        // Если ячейка пустая, очищаем содержимое
                        setGraphic(null);
                    } else {
                        // Иначе устанавливаем ChoiceBox в ячейку
                        choiceBox.getSelectionModel().select(item);
                        setGraphic(choiceBox);
                    }
                }
            };
        });
    }

    // Заполнение стобца с потенциальным доходом
    private void calculatePotentialProfitPercent() {
        potentialProfitPercentColumn.setCellValueFactory(cellData -> {
            Skin skin = cellData.getValue();
            double potentialPercent = 0;
            if (skin.getSalePrice() == 0) {
                potentialPercent = ((double) Math.round((skin.getCurrentPrice() - skin.getPurchasePrice()) / skin.getPurchasePrice() * 10000)) / 100;
            }
            return new ReadOnlyObjectWrapper<>(potentialPercent);
        });
    }

    // Графики
    @FXML
    protected void onChartsButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Charts.fxml"));
        Parent root = loader.load();

        // Получаем экземпляр контроллера из загруженного FXML-файла
        ControllerCharts controllerCharts = loader.getController();

        // Вызываем метод profitOfType на экземпляре контроллера, передавая портфолио
        controllerCharts.profitOfType(portfolio);
        controllerCharts.drawAvgPercentProfitOfType(portfolio);

        // Создаем новое окно
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/diplom/pictures/logo.png")));
        Scene scene = new Scene(root);

        // Подключаем CSS-файл
        scene.getStylesheets().add(getClass().getResource("/com/example/diplom/css/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Графики");
        stage.initModality(Modality.APPLICATION_MODAL); // Устанавливаем модальное окно

        // Показываем новое окно
        stage.show();
    }

    // Планирование
    @FXML
    private void onPlanningButtonClick(ActionEvent event) throws IOException {
        if (portfolio.size() >= 3) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Planner.fxml"));
            Parent root = loader.load();

            // Загружаем CSS-файл и применяем его к сцене
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/diplom/css/styles.css").toExternalForm());

            // Получаем экземпляр контроллера из загруженного FXML-файла
            ControllerPlanner controllerPlanner = loader.getController();
            controllerPlanner.recType(portfolio);
            controllerPlanner.lowPriceItems(portfolio);
            controllerPlanner.recSaleItems(portfolio);
            controllerPlanner.recBuyItems(portfolio);

            // Создаем новое окно
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/diplom/pictures/logo.png")));
            // Устанавливаем сцену для окна
            stage.setScene(scene);
            stage.setTitle("Планирование");
            stage.initModality(Modality.APPLICATION_MODAL); // Устанавливаем модальное окно

            // Показываем новое окно
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Сообщение");
            alert.setHeaderText("Недостаток элементов для плаинрования");
            alert.setContentText("В вашем портфолио слишком мало элементов для планирования. Их должно быть хотя бы 3.");
            // Отображаем диалоговое окно и ждем, пока пользователь его закроет
            alert.showAndWait();
        }
    }

    // Сохранение портфолио в файл
    @FXML
    private void portfolioSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        File file = fileChooser.showSaveDialog(null); // Передайте null, чтобы использовать главное окно приложения
        if (file != null & portfolio.size() != 0) {
            PortfolioSerializer.savePortfolio(portfolio, file.getPath());
            System.out.println("Portfolio saved to: " + file.getAbsolutePath());
        }
    }

    // Загрузка портфолио из файла
    @FXML
    private void portfolioLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить файл");
        File file = fileChooser.showOpenDialog(null); // null, чтобы использовать главное окно приложения
        if (file != null) {
            portfolio.clear(); // Очищаем текущий список портфолио
            List<Skin> loadedPortfolio = PortfolioSerializer.loadPortfolio(file.getPath()); // Загружаем портфолио из файла
            if (loadedPortfolio != null) {
                portfolio.addAll(loadedPortfolio); // Добавляем загруженные данные в портфолио
                System.out.println("Portfolio loaded from: " + file.getAbsolutePath());
                skinsTable.setItems(portfolio);
                // Расчет и вывод общих показателей
                calculationOfGeneralparameters();
            } else {
                System.out.println("Failed to load portfolio from: " + file.getAbsolutePath());
            }
        }
    }

    // Добавление кнопок Удалить для удаление скинов в столбец
    private void addDeleteButtonToTable() {
        Callback<TableColumn<Skin, Void>, TableCell<Skin, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Skin, Void> call(final TableColumn<Skin, Void> param) {
                final TableCell<Skin, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Удалить");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Skin skin = getTableView().getItems().get(getIndex());
                            portfolio.remove(skin);
                            if (portfolio.isEmpty()) {
                                investedLabel.setText("Вложено: ");
                                revenueLabel.setText("Выручка: ");
                                profitLabel.setText("Прибыль: ");
                                profitPercentLabel.setText("Прибыль, %: ");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    // Расчет общих показателей: общие вложения, выручка, доход, доход %
    private void calculationOfGeneralparameters() {
        if (portfolio.size() > 0) {
            double investedSum = 0.0;
            double revenueSum = 0.0;
            double profitSum = 0.0;
            double profitPercentSum = 0.0;
            for (Skin skin : portfolio) {
                investedSum += skin.purchasePrice;
                revenueSum += skin.salePrice;
                profitSum += skin.profit;
                profitPercentSum += skin.profitPercent;
            }
            investedLabel.setText("Вложено: " + Double.toString(((double) Math.round(investedSum * 100) / 100)));
            revenueLabel.setText("Выручка: " + Double.toString(((double) Math.round(revenueSum * 100) / 100)));
            profitLabel.setText("Прибыль: " + Double.toString(((double) Math.round(profitSum * 100) / 100)));
            profitPercentLabel.setText("Прибыль, %: " + Double.toString(((double) Math.round(profitPercentSum * 100) / 100)));
        }
    }
}

