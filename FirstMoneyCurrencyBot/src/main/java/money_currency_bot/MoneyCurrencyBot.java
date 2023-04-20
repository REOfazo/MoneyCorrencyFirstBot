package money_currency_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MoneyCurrencyBot extends TelegramLongPollingBot {
    private byte s = 0;
    private String ccyName;
    private double ccySalary;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        SendMessage message = new SendMessage();
        URL url = new URL("https://cbu.uz/uz/arkhiv-kursov-valyut/json/");
        URLConnection connection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        CentralBankApi[] centralBankApi = gson.fromJson(bufferedReader, CentralBankApi[].class);
        message.setChatId(update.getMessage().getChatId());

        if (update.hasMessage() && text.equals("/start")) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            KeyboardRow keyboardRow, keyboardRow1, keyboardRow2, keyboardRow3;
            KeyboardButton button, button1, button2, button3;
            List<KeyboardRow> keyboardButtonList = new ArrayList<>();

            keyboardRow = new KeyboardRow();
            keyboardRow1 = new KeyboardRow();
            keyboardRow2 = new KeyboardRow();
            keyboardRow3 = new KeyboardRow();

            button = new KeyboardButton();
            button1 = new KeyboardButton();
            button2 = new KeyboardButton();
            button3 = new KeyboardButton();

            keyboardRow.add(button);
            keyboardRow1.add(button1);
            keyboardRow2.add(button2);
            keyboardRow3.add(button3);

            button.setText("Valyuta narxlarini olish");
            button1.setText("So'mdan valyutaga");
            button2.setText("Valyutadan so'mga");
            button3.setText("Show \"/start\"");

            keyboardButtonList.add(keyboardRow);
            keyboardButtonList.add(keyboardRow1);
            keyboardButtonList.add(keyboardRow2);
            keyboardButtonList.add(keyboardRow3);
            replyKeyboardMarkup.setKeyboard(keyboardButtonList);

            replyKeyboardMarkup.setResizeKeyboard(true);

            message.setText("Assalamu alaykum!");

            message.setReplyMarkup(replyKeyboardMarkup);
            execute(message);
        } else if (text.equals("Valyuta narxlarini olish")) {
            for (CentralBankApi bankApi : centralBankApi) {
                if (bankApi.getCcy().equals("USD") || bankApi.getCcy().equals("EUR") || bankApi.getCcy().equals("CNY")) {
                    message.setText(bankApi.getCcyNm_UZ() + "  :  " + bankApi.getRate() + " so'm");
                    execute(message);
                }
            }
        } else if (text.equals("So'mdan valyutaga")) {
            message.setText("Valyutani tanlang (masalan, USD)");
            execute(message);
            s ++ ;
        } else if (text.equals("Valyutadan so'mga")) {
            message.setText("Valyutani tanlang (masalan, USD)");
            execute(message);
            s += 2 ;
        } else if (text.equals("Show \"/start\"")) {
            message.setText("/start");
            execute(message);
        } else if (s == 1) {
            ccyName = update.getMessage().getText();
            message.setText("Miqdorni kiriting: (so'mda)");
            execute(message);
            s = 3;
        } else if (s == 2) {
            ccyName = update.getMessage().getText();
            message.setText("Miqdorni kiriting: (masalan, " + update.getMessage().getText() + ")");
            execute(message);
            s = 3;
        } else if (s == 3) {
            ccySalary = Double.parseDouble(update.getMessage().getText());
            for (CentralBankApi bankApi : centralBankApi) {
                if (bankApi.getCcy().equals(ccyName)) {
                    message.setText(bankApi.getCcyNm_UZ() + "  :  " + ccySalary / Double.parseDouble(bankApi.getRate()) + " " + bankApi.getCcy());
                    execute(message);
                }
            }
            s = 0;
        } else if (s == 4) {
            ccySalary = Double.parseDouble(update.getMessage().getText());
            for (CentralBankApi bankApi : centralBankApi) {
                if (bankApi.getCcy().equals(ccyName)) {
                    message.setText(bankApi.getCcyNm_UZ() + "  :  " + Double.parseDouble(bankApi.getRate()) / ccySalary + " so'm");
                    execute(message);
                }
            }
            s = 0;
        } else {
            message.setText("Iltimos ko'rsatilgan maydonlardan birini tanlang!");
            execute(message);
        }

    }

    @Override
    public String getBotUsername() {
        return "http://t.me/MoneyCurrencyFirstBot";
    }
    @Override
    public String getBotToken() {
        return "5909743321:AAEUQd2vc9zzlWEYJi3TanXS5ZNsrgrfWvY";
    }
}
