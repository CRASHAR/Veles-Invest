package com.example.diplom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PortfolioSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void savePortfolio(ObservableList<Skin> portfolio, String filepath) {
        try {
            objectMapper.writeValue(new File(filepath), portfolio);
            System.out.println("Portfolio saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Skin> loadPortfolio(String filepath) {
        List<Skin> portfolio = null;
        try {
            portfolio = objectMapper.readValue(new File(filepath), new TypeReference<List<Skin>>() {});
            System.out.println("Portfolio loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return portfolio;
    }
}
