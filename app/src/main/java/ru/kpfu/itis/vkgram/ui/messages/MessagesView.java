package ru.kpfu.itis.vkgram.ui.messages;

import android.support.annotation.NonNull;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.vk.sdk.api.model.VKApiMessage;
import io.reactivex.disposables.Disposable;
import java.util.List;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.Messages;
import ru.kpfu.itis.vkgram.model.VKChatMessage;

@StateStrategyType(SkipStrategy.class)
public interface MessagesView extends MvpView {

    @StateStrategyType(AddToEndStrategy.class)
    void addMoreItems(List<Object> items);

    @StateStrategyType(AddToEndStrategy.class)
    void addMoreItems(Chat item);

    void handleError(Throwable error);

    void hideLoading();

    void setNotLoading();

    void showDetails(VKChatMessage item);

    void showDetails(Chat item);

    @StateStrategyType(AddToEndStrategy.class)
    void showItems(@NonNull List<Object> items);

    void showLoading(Disposable disposable);
}
