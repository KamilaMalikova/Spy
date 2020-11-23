package com.spy.spydemo;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.checkerframework.checker.units.qual.A;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TelegramAPI  {
    String token;
    String chat_id;
    String path;
    String content_type;
    String method;

    public TelegramAPI(String token, String chat_id, String path) {
      //  super(new DefaultBotOptions());
        //super();
        this.token = token;
        this.chat_id = chat_id;
        this.path = path;
    }

    public CloseableHttpResponse sendDocUploadingAFile(File save, String caption) throws TelegramApiException, IOException {

        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chat_id);
        sendDocumentRequest.setDocument(new InputFile(save));
        sendDocumentRequest.setCaption(caption);
        return sendDocument(sendDocumentRequest);
    }

    private CloseableHttpResponse sendDocument(SendDocument sendDocumentRequest) throws TelegramApiException, IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost upload = new HttpPost("https://api.telegram.org/bot"+this.token+"/sendDocument?chat_id="+this.chat_id);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        File file = new File(path);

        builder.addBinaryBody(
                "document",
                new FileInputStream(file));

        HttpEntity part = builder.build();

        upload.setEntity(part);

        CloseableHttpResponse response = client.execute(upload);

        return response;

    }

}
