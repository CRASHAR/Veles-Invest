package com.example.diplom;

import javafx.collections.ObservableList;

public class PortfolioStat {
    double avgPercentCasesProfit;
    double avgPercentWeaponsProfit;
    double avgPercentStickersProfit;
    double avgPercentOtherProfit;
    int columnCases;
    int columnWeapons;
    int columnStickers;
    int columnOther;
    double maxAvgProfit;
    String maxAvgProfitType;
    String[] types = {"Кейс", "Оружие", "Стикер", "Другое"};
    double[] values;


    // Конструктор по умолчанию
    public PortfolioStat() {

    }

    // Возврат наибольшего по проценту дохода типа предмета
    public String getMaxPercentProfitType(ObservableList<Skin> tempPortfolio) {
        avgPercentProfitOfType(tempPortfolio);
        values = new double[]{avgPercentCasesProfit, avgPercentWeaponsProfit, avgPercentStickersProfit, avgPercentOtherProfit};
        maxAvgProfit = values[0];
        maxAvgProfitType = types[0];

        for (int i = 1; i < values.length; i++) {
            if (values[i] > maxAvgProfit) {
                maxAvgProfit = values[i];
                maxAvgProfitType = types[i];
            }
        }
        return maxAvgProfitType;
    }

    // Подсчет среднего процента доходности по каждому типу предметов
    public void avgPercentProfitOfType(ObservableList<Skin> tempPortfolio) {
        avgPercentCasesProfit = 0.0;
        avgPercentWeaponsProfit = 0.0;
        avgPercentStickersProfit = 0.0;
        avgPercentOtherProfit = 0.0;
        columnCases = 0;
        columnWeapons = 0;
        columnStickers = 0;
        columnOther = 0;
        for (Skin skin : tempPortfolio) {
            if (skin.getItemType() != null & skin.getProfit() != 0) {
                switch (skin.getItemType()) {
                    case "Кейс":
                        avgPercentCasesProfit += skin.getProfitPercent();
                        columnCases++;
                        break;
                    case "Оружие":
                        columnWeapons++;
                        avgPercentWeaponsProfit += skin.getProfitPercent();
                        break;
                    case "Стикер":
                        avgPercentStickersProfit += skin.getProfitPercent();
                        columnStickers++;
                        break;
                    case "Другое":
                        avgPercentOtherProfit += skin.getProfitPercent();
                        columnOther++;
                        break;
                }
            }
        }
        if (columnCases != 0) {
            avgPercentCasesProfit = avgPercentCasesProfit / columnCases;
        }
        if (columnWeapons != 0) {
            avgPercentWeaponsProfit = avgPercentWeaponsProfit / columnWeapons;
        }
        if (columnStickers != 0) {
            avgPercentStickersProfit = avgPercentStickersProfit / columnStickers;
        }
        if (columnOther != 0) {
            avgPercentOtherProfit = avgPercentOtherProfit / columnOther;
        }
    }
}
