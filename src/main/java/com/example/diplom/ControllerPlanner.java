package com.example.diplom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerPlanner implements Initializable {
    @FXML
    private TextArea recText;
    private ObservableList<Skin> reducedPriceItems = FXCollections.observableArrayList();     // Предметы, которые стоят дешевле чем их купили
    private ObservableList<Skin> expensiveItems = FXCollections.observableArrayList();;               // Предметы, которые стоят дороже чем их купили
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // Прибыльные категории предметов (у которых наивысший % прибыльности)
    void recType(ObservableList<Skin> tempPortfolio) {
        // Создаем экзэмляр Статистики по портфолио и получаем самый прибыльный по % тип предметов
        PortfolioStat portfolioStat = new PortfolioStat();
        portfolioStat.getMaxPercentProfitType(tempPortfolio);
        if (portfolioStat.maxAvgProfit != 0) {
            recText.setText("   Стоит обратить внимание на тип предмета: " + portfolioStat.getMaxPercentProfitType(tempPortfolio) + ". По вашему портфелю он дал вам наибольший средний процент дохода: " +  Double.toString((double) Math.round(portfolioStat.maxAvgProfit * 100) / 100) + ". Рассмотрите возможность увеличения " +
                    "вложений в данный тип предметов, однако не забывайте про диверсификацию!\n");
        }
    }

    // Предметы, на которые стоит обратить внимание продать/докупить
    void lowPriceItems(ObservableList<Skin> tempPortfolio) {
        for (Skin skin : tempPortfolio) {
            if (skin.getPurchasePrice() > skin.getCurrentPrice() & skin.getSalePrice() == 0) {
                reducedPriceItems.add(skin);
            }
        }
        if (reducedPriceItems.size() > 0) {
            recText.setText(recText.getText() + "   \nОбратите внимание на следующие предметы:\n");
            for (Skin skin : reducedPriceItems) {
                recText.setText(recText.getText() + "   -> " + skin.getItemName() + "\n");
            }
            recText.setText(recText.getText() + "   Эти позиции в вашем портфеле на данный момент имеют рыночную стоимость ниже чем стоимость их приобретения. Если вы вы считаете, что они вырастут, то имеет смысл докупить эти позиции. В ином случае задумайтесь над их продажей," +
                    " пока просадка в цене не стала более критичной.\n");
        }
    }

    // Предметы, на которые стоит обратить внимание (продать)
    void recSaleItems(ObservableList<Skin> tempPortfolio) {
        for (Skin skin : tempPortfolio) {
            if (skin.getPurchasePrice() < skin.getCurrentPrice() & skin.getSalePrice() == 0) {
                expensiveItems.add(skin);
            }
        }
        if (expensiveItems.size() > 0) {
            recText.setText(recText.getText() + "   \nОбратите внимание на следующие предметы:\n");
            for (Skin skin : expensiveItems) {
                recText.setText(recText.getText() + "   -> " + skin.getItemName() + "\n");
            }
            recText.setText(recText.getText() + "   У данных предметов нынешняя стоиомость выше, чем та, за которую вы их покупали. Стоит рассмотреть фиксацию прибыли или возможно для их дальнейшего роста.\n");
        }
    }

    // Предметы, которые приносили хорошие проценты и их снова можно купить
    void recBuyItems(ObservableList<Skin> tempPortfolio) {
        double maxPercent = 0;
        int maxPercentIndex = -1;
        for (Skin skin : tempPortfolio) {
            if (skin.getProfitPercent() > maxPercent) {
                maxPercent = skin.getProfitPercent();
                maxPercentIndex = tempPortfolio.indexOf(skin);
            }
        }
        if (maxPercentIndex != -1) {
            recText.setText(recText.getText() + "   \n  В вашем портфлеле самая выгодная в процентном отношении была сделка с предметом: " + tempPortfolio.get(maxPercentIndex).getItemName() + " — " + tempPortfolio.get(maxPercentIndex).getProfitPercent() + "%. " +
                    "На рынке есть еще такие предметы. Рассмотрите вариант с возможной повторной инвестицией в данный актив.");
        }
    }

}
