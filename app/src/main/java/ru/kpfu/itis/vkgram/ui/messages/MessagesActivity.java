package ru.kpfu.itis.vkgram.ui.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.arellomobile.mvp.presenter.InjectPresenter;
import io.reactivex.disposables.Disposable;
import java.util.*;
import org.drinkless.td.libcore.telegram.TdApi.Chat;
import ru.kpfu.itis.vkgram.R;
import ru.kpfu.itis.vkgram.model.VKChatMessage;
import ru.kpfu.itis.vkgram.ui.base.*;
import ru.kpfu.itis.vkgram.ui.chat.ChatActivity;
import ru.kpfu.itis.vkgram.widget.EmptyStateRecyclerView;

public class MessagesActivity extends BaseActivity implements MessagesView, BaseAdapter.OnItemClickListener<Object> {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EmptyStateRecyclerView recyclerView;

    @InjectPresenter
    MessagesPresenter presenter;

    private MessagesAdapter adapter;

    private boolean isLoading = false;

    public static void start(@NonNull Activity activity){
        Intent intent = new Intent(activity, MessagesActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_messages, contentFrameLayout);
        initViews();
        initRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.refresh:
                presenter.loadMessages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addMoreItems(List<Object> items) {
        adapter.addAll(items);
    }

    @Override
    public void addMoreItems(Chat item) {
        adapter.addAll(Collections.singletonList(item));
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(@NonNull Object item) {
        if (item instanceof VKChatMessage){
            presenter.onItemClick((VKChatMessage) item);
        } else {
            presenter.onItemClick((Chat) item);
        }
    }

    @Override
    public void setNotLoading() {
        isLoading = false;
    }

    @Override
    public void showDetails(VKChatMessage item) {
        ChatActivity.start(this, item);
    }

    @Override
    public void showDetails(Chat item) {
        ChatActivity.start(this, item);
    }

    @Override
    public void showItems(@NonNull List<Object> items) {
        adapter.changeDataSet(items);
    }

    @Override
    public void showLoading(Disposable disposable) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void initViews(){
        toolbar = findViewById(R.id.tb_messages);
        progressBar = findViewById(R.id.pb_message_list);
        recyclerView = findViewById(R.id.rv_message_list);
        supportActionBar(toolbar);
    }

    private void initRecycler() {
        adapter = new MessagesAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(this);
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
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 20) {
                        isLoading = true;
                        presenter.loadNextElements(++currentPage);
                    }
                }
            }
        });
    }
}
