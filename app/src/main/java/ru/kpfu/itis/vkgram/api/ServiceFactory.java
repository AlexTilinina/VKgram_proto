package ru.kpfu.itis.vkgram.api;

public class ServiceFactory {

    private static MessageService messageService;
    private static TelegramService telegramService;

    public static MessageService getMessageService(){
        if (messageService == null){
            messageService = new MessageServiceImpl();
        }
        return messageService;
    }

    public static TelegramService getTelegramService(){
        if (telegramService == null){
            telegramService = new TelegramService();
        }
        return telegramService;
    }
}
