package com.example.diplom;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Skin {
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    //Цена покупки
    public double getPurchasePrice() {
        return purchasePrice;
    }

    //Цена покупки
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    //Текущая цена
    public double getCurrentPrice() {
        return currentPrice;
    }

    //Цена продажи
    public double getSalePrice() {
        return salePrice;
    }

    //Цена продажи
    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    // Доход от продажи
    public double getProfit() {return profit;}
    public void setProfit(double profit) {this.profit = profit;}

    // Доход от продажи проценты
    public double getProfitPercent() {return profitPercent;}
    public void setProfitPercent(double profitPercent) {this.profitPercent = profitPercent;}

    // Тип предмета
    public String getItemType() {return itemType;}
    public void setItemType(String itemType) {this.itemType = itemType;}

    // Вывод всей информации
    public void getAllInfo() {
        System.out.print(itemName + " " + purchasePrice + " " + salePrice + " " + profit + " " + itemType + "\n");
    }

    public double purchasePrice;        // Цена покупки
    public double currentPrice;         // Текущая цена
    public String itemName;             // Название скина
    public double salePrice;            // Цена продажи
    public double profit;               // Выгода от продажи
    public double profitPercent;        // Выгода от продажи
    public String itemType;             // Тип предмета

    private static final OkHttpClient httpClient = new OkHttpClient();

    public Skin(String itemName, double purchasePrice) {
        this.purchasePrice = purchasePrice;
        this.itemName = itemName;
    }
    // Конструктор по умолчанию
    public Skin() {
    }

    //Текущая цена
    public void setCurrentValue() {
        String marketHashName = "{\"market_hash_name\":\"" + itemName; // Название предмета

        // Формируем URL запроса
        String url = "https://market.csgo.com/api/v2/prices/RUB.json";

        // Выполняем GET запрос
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();

            // Обработка ответа
            // В responseBody содержится JSON с данными о ценах предметов
            // Индекс конца ответа по нужному скину
            int endOfTheResponce = responseBody.indexOf(marketHashName);
            while (responseBody.charAt(endOfTheResponce) != '}') {
                endOfTheResponce++;
            }
            // Находим стоиомость и отделяем ее в инофрмации о скине
            String skinInfo = responseBody.substring(responseBody.indexOf(marketHashName), endOfTheResponce + 1);
            System.out.println(skinInfo);
            currentPrice = Double.parseDouble(skinInfo.substring(skinInfo.indexOf("price\":\"") + "price\":\"".length(), skinInfo.length() - 2));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
