package ru.kpfu.itis.vkgram.ui.chat;

import android.support.annotation.NonNull;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.vk.sdk.api.VKError;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.Messages;
import ru.kpfu.itis.vkgram.model.VKChatMessage;

@StateStrategyType(SkipStrategy.class)
public interface ChatView extends MvpView{

    void getChatId();

    void handleError(Throwable error);

    void setChatTitle();

    @StateStrategyType(AddToEndStrategy.class)
    void showItems(@NonNull List<VKChatMessage> items);

    @StateStrategyType(AddToEndStrategy.class)
    void showItems(@NonNull Messages items);

    @StateStrategyType(AddToEndStrategy.class)
    void addMoreItems(List<VKChatMessage> items);

    @StateStrategyType(AddToEndStrategy.class)
    void addMoreItems(Messages items);

    void setMessageSending();

    void setMessageSent();

    void error(VKError error);
}
