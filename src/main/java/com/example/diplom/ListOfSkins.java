package com.example.diplom;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListOfSkins {
    private static final OkHttpClient httpClient = new OkHttpClient();
    public String[] list;

    public void setListOfSkins() {
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

            // Создаем паттерн для поиска значений market_hash_name и того, что находится после двоеточия в кавычках
            Pattern pattern = Pattern.compile("\"market_hash_name\":\"([^\"]+)\",\"([^:]+)\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(responseBody);

            List<String> names = new ArrayList<>();

            // Находим все совпадения
            while (matcher.find()) {
                // Группа 1 содержит имя
                String name = matcher.group(1).trim();
                names.add(name);
            }
            // Преобразуем список в массив строк
           list = names.toArray(new String[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
