package ru.kpfu.itis.vkgram.ui.auth.telegram;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(SkipStrategy.class)
public interface TelegramAuthView extends MvpView {

    void changeViewSingUp();

    void handleError(Throwable error);

    void changeViewSingIn();
}
