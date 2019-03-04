package ru.kpfu.itis.vkgram.ui.chat;

import static ru.kpfu.itis.vkgram.utils.Constants.*;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.vk.sdk.api.VKError;
import java.util.*;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import org.drinkless.td.libcore.telegram.TdApi.FormattedText;
import org.drinkless.td.libcore.telegram.TdApi.Message;
import org.drinkless.td.libcore.telegram.TdApi.MessageText;
import org.drinkless.td.libcore.telegram.TdApi.Messages;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.ui.base.BaseActivity;
import ru.kpfu.itis.vkgram.widget.EmptyStateRecyclerView;

public class ChatActivity extends BaseActivity implements ChatView, OnClickListener {

    private Toolbar toolbar;
    private ImageView ivAddSticker;
    private ImageView ivAddAttachment;
    private ImageView ivSend;
    private EditText etMessage;
    private EmptyStateRecyclerView recyclerView;

    private long id;

    private VKChatMessage message;

    private String title;

    @InjectPresenter
    ChatPresenter presenter;

    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_chat, frameLayout);
        initViews();
        initRecycler();

        message = getIntent().getParcelableExtra(MESSAGE);
        if (message != null){
            int chatId = message.getChatId();
            id = chatId != 0 ? chatId + CHAT_PREFIX : message.user_id;
            setChatTitle();
        } else {
            id = getIntent().getLongExtra(CHAT_ID, 0);
            title = getIntent().getStringExtra(TITLE);
            Log.d("START", "onCreate: " + id);
            setChatTitle();
        }
    }

    public static void start(@NonNull Activity activity, @NonNull Object message) {
        Intent intent = new Intent(activity, ChatActivity.class);
        if (message instanceof VKChatMessage){
            intent.putExtra(MESSAGE,(VKChatMessage) message);
        }
        else {
            if (message instanceof Chat){
                Chat chat = (Chat) message;
                Log.d("START", "start: " + chat.id);
                intent.putExtra(CHAT_ID, chat.id);
                intent.putExtra(TITLE, chat.title);
            }
        }
        activity.startActivity(intent);
    }

    @Override
    public void getChatId() {
        presenter.loadChatMessages(id, message != null);
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_send:
                if (message != null){
                    VKChatMessage vkChatMessage = new VKChatMessage();
                    vkChatMessage.body = etMessage.getText().toString();
                    vkChatMessage.user_id = message.user_id;
                    vkChatMessage.setChatId(message.getChatId());
                    vkChatMessage.out = true;
                    presenter.sendMessage(vkChatMessage);
                    adapter.addToBeginning(vkChatMessage);
                }
                else {
                    //TODO отправка сообщения
                    Message message = new Message();

                    FormattedText ft = new FormattedText();
                    ft.text = etMessage.getText().toString();
                    MessageText mt = new MessageText();
                    mt.text = ft;

                    message.content = mt;
                    message.chatId = this.id;
                    message.isOutgoing = true;
                    presenter.sendMessage(message);
                    adapter.addToBeginning(message);
                }
                break;
            case R.id.et_message:
                recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void setChatTitle() {
        String title;
        if (message != null){
            title = message.title;
        } else {
          title = this.title;
        }
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void showItems(@NonNull List<VKChatMessage> items) {
        adapter.changeDataSet(items);
    }

    @Override
    public void showItems(@NonNull Messages items) {
        adapter.changeDataSet(Arrays.asList(items.messages));
    }

    @Override
    public void addMoreItems(List<VKChatMessage> items) {
        adapter.addAll(items);
    }

    @Override
    public void addMoreItems(Messages items) {
        adapter.addAll(Arrays.asList(items.messages));
    }

    @Override
    public void setMessageSending() {
        etMessage.setText("");
        ((ChatMessageItemHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(0))).setSending();
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void setMessageSent() {
        //TODO notify message activity
        //TODO остальные непрочитанные должны отметиться прочитанными
        ((ChatMessageItemHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(0))).setCurrentTime();
    }

    @Override
    public void error(VKError error) {
        Log.d("TAK", "error: " + error.errorReason);
        Toast.makeText(this, error.errorCode + "", Toast.LENGTH_LONG).show();
    }

    private void initViews() {
        toolbar = findViewById(R.id.tb_chat);
        ivAddSticker = findViewById(R.id.iv_add_sticker);
        ivAddAttachment = findViewById(R.id.iv_add_attachment);
        ivSend = findViewById(R.id.iv_send);
        etMessage = findViewById(R.id.et_message);
        recyclerView = findViewById(R.id.rv_message_list);
        supportActionBar(toolbar);
        setBackArrow(toolbar);
        ivSend.setOnClickListener(this);
    }

    private void initRecycler() {
        adapter = new ChatMessageAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        adapter.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentPage = 0;
            private boolean isLastPage = false;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (!isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 20) {
                        presenter.loadNextElements(++currentPage, message != null);
                    }
                }
            }
        });
        recyclerView.scrollToPosition(0);
    }
}
