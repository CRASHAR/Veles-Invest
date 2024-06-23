package com.example.diplom;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCharts implements Initializable {
    @FXML
    private PieChart pieChartProfitOfType;
    @FXML
    private Button goMainScene;
    @FXML
    private Label labelTotalProfit;
    @FXML
    private BarChart barChartAvgPercentProfit;
    private double casesProfit;
    private double weaponsProfit;
    private double stickersProfit;
    private double otherProfit;
    private double totalProfit;
    private double countOfSkins;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Вывод Круговой диаграммы с распределением дохода по типам
    public void profitOfType(ObservableList<Skin> tempPortfolio) {
        casesProfit = 0.0;
        weaponsProfit = 0.0;
        stickersProfit = 0.0;
        otherProfit = 0.0;
        totalProfit = 0.0;
        for (Skin skin : tempPortfolio) {
            if (skin.getItemType() != null) {
                switch (skin.getItemType()) {
                    case "Кейс":
                        casesProfit += skin.getProfit();
                        break;
                    case "Оружие":
                        weaponsProfit += skin.getProfit();
                        break;
                    case "Стикер":
                        stickersProfit += skin.getProfit();
                        break;
                    case "Другое":
                        otherProfit += skin.getProfit();
                        break;
                }
            }
        }
        totalProfit = casesProfit + weaponsProfit + stickersProfit + otherProfit;

        if (casesProfit > 0) {
            PieChart.Data slice1 = new PieChart.Data("Кейсы", casesProfit);
            pieChartProfitOfType.getData().add(slice1);
        }
        if (weaponsProfit > 0) {
            PieChart.Data slice2 = new PieChart.Data("Оружие", weaponsProfit);
            pieChartProfitOfType.getData().add(slice2);
        }
        if (stickersProfit > 0) {
            PieChart.Data slice3 = new PieChart.Data("Стикеры", stickersProfit);
            pieChartProfitOfType.getData().add(slice3);
        }
        if (otherProfit > 0) {
            PieChart.Data slice4 = new PieChart.Data("Другое", otherProfit);
            pieChartProfitOfType.getData().add(slice4);
        }
        if (totalProfit > 0) {
            labelTotalProfit.setText("Общая прибыль: " + Double.toString((double) Math.round(totalProfit * 100) / 100));
        }
    }

    // Вывод гистограммы с данными о средней прибыли в процентах по каждому типу предметов
    public void drawAvgPercentProfitOfType(ObservableList<Skin> tempPortfolio) {
        PortfolioStat portfolioStat = new PortfolioStat();
        portfolioStat.avgPercentProfitOfType(tempPortfolio);
        if (portfolioStat.columnCases != 0) {
            XYChart.Series<String, Number> seriesCases = new XYChart.Series<>();
            seriesCases.setName("Кейсы");
            seriesCases.getData().add(new XYChart.Data<>("Кейсы", portfolioStat.avgPercentCasesProfit));
            barChartAvgPercentProfit.getData().add(seriesCases);
        }
        if (portfolioStat.columnWeapons != 0) {
            XYChart.Series<String, Number> seriesWeapons = new XYChart.Series<>();
            seriesWeapons.setName("Оружие");
            seriesWeapons.getData().add(new XYChart.Data<>("Оружие", portfolioStat.avgPercentWeaponsProfit));
            barChartAvgPercentProfit.getData().add(seriesWeapons);
        }
        if (portfolioStat.columnStickers != 0) {
            XYChart.Series<String, Number> seriesStickers = new XYChart.Series<>();
            seriesStickers.setName("Стикеры");
            seriesStickers.getData().add(new XYChart.Data<>("Стикеры", portfolioStat.avgPercentStickersProfit));
            barChartAvgPercentProfit.getData().add(seriesStickers);

        }
        if (portfolioStat.columnOther != 0) {
            XYChart.Series<String, Number> seriesOther = new XYChart.Series<>();
            seriesOther.setName("Другие");
            seriesOther.getData().add(new XYChart.Data<>("Другие", portfolioStat.avgPercentOtherProfit));
            barChartAvgPercentProfit.getData().add(seriesOther);
        }
    }

}
