package com.aeliseev.telegram.bot;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Component
public class PhotoBot extends TelegramLongPollingBot {

    public PhotoBot(@Value("${telegrambots.tokens.ae.photo:}") String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return "AETutorialPhotoBot";
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                String[] messageText = update.getMessage().getText().trim().split("\\s+");
                List<String> aa = Arrays.stream(messageText)
                        .filter(StringUtils::isNotBlank)
                        .toList();
                if ("/pic".equals(aa.get(0))) {
                    sendPhotoById(chatId, aa.get(1));
                }
            } else if (update.getMessage().hasPhoto()) {
                sendBackPhoto(chatId, update);
            }
        }
    }

    @SneakyThrows
    private void sendBackPhoto(long chatId, Update update) {
        Optional<PhotoSize> maxPhoto = update.getMessage().getPhoto().stream()
                .max(Comparator.comparing(PhotoSize::getFileSize));
        String f_id = maxPhoto.map(PhotoSize::getFileId).orElse(null);
        Integer f_width = maxPhoto.map(PhotoSize::getWidth).orElse(null);
        Integer f_height = maxPhoto.map(PhotoSize::getHeight).orElse(null);

        // Set photo caption
        String caption = "Uploaded file to TG servers \nfile_id: " + f_id + "\nwidth: " + f_width + "\nheight: " + f_height;
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);

        InputFile inputFile = new InputFile();
        inputFile.setMedia(getFileById(f_id), "some_file_from_tg");
        sendPhoto.setPhoto(inputFile);

        execute(sendPhoto);
    }

    @SneakyThrows
    private void sendPhotoById(long chatId, String fileId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption("file_id: " + fileId);

        InputFile inputFile = new InputFile();
        inputFile.setMedia(getFileById(fileId), "some_file_from_tg");
        sendPhoto.setPhoto(inputFile);

        execute(sendPhoto);
    }

    @SneakyThrows
    private java.io.File getFileById(String fileId) {
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        File file = execute(getFileMethod);
        String filePath = file.getFilePath();
        return downloadFile(filePath);
    }

}
