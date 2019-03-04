package ru.kpfu.itis.vkgram.repository;

import android.support.annotation.MainThread;

public class RepositoryProvider {

    private static MessageRepository mMessageRepository;

    public static MessageRepository provideMessageRepository(){
        if (mMessageRepository == null){
            mMessageRepository = new MessageRepositoryImpl();
        }
        return mMessageRepository;
    }

    @MainThread
    public static void init() {
        mMessageRepository = new MessageRepositoryImpl();
    }

    public static void setMessageRepository(MessageRepository messageRepository) {
        mMessageRepository = messageRepository;
    }
}
