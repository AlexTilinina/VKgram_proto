package ru.kpfu.itis.vkgram.api;

import org.drinkless.td.libcore.telegram.Client;
import ru.kpfu.itis.vkgram.api.tg_handler.DefaultHandler;

public class TelegramService {

    private Client client;

    public Client getClient(){
        if (client == null){
            client = Client.create(new DefaultHandler(), null, null);
        }
        return client;
    }
}
