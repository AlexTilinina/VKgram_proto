package ru.kpfu.itis.vkgram.ui.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ru.kpfu.itis.vkgram.widget.EmptyStateRecyclerView;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final List<T> items = new ArrayList<>();

    @Nullable
    private EmptyStateRecyclerView recyclerView;

    @Nullable
    private OnItemClickListener<T> onItemClickListener;

    public List<T> getItems() {
        return items;
    }

    private final View.OnClickListener listener = (view) -> {
        if (onItemClickListener != null) {
            int position = (int) view.getTag();
            T item = items.get(position);
            onItemClickListener.onItemClick(item);
        }
    };

    public BaseAdapter(@NonNull List<T> items) {
        this.items.addAll(items);
    }

    public void attachToRecyclerView(@NonNull EmptyStateRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.setAdapter(this);
        refreshRecycler();
    }

    public final void add(@NonNull T value) {
        items.add(value);
        refreshRecycler();
    }

    public final void addToBeginning(@NonNull T value) {
        items.add(0, value);
        refreshRecycler();
    }

    public final void changeDataSet(@NonNull List<? extends T> values) {
        items.clear();
        items.addAll(values);
        refreshRecycler();
    }

    public void addAll(@NonNull List<? extends T> values) {
        for (T value : values) {
            items.add(value);
            notifyItemInserted(items.size() - 1);
        }
    }

    public final void clear() {
        items.clear();
        refreshRecycler();
    }

    protected void refreshRecycler() {
        notifyDataSetChanged();
        if (recyclerView != null) {
            recyclerView.checkIfEmpty();
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(listener);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(@NonNull T item);
    }

    public interface OnItemLongClickListener<T>{
        void onItemLongClick(@NonNull T item);
    }
}
