package com.example.chatpet.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpet.R;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.util.TimeUtils;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Context context;

    public ChatAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.tvSender.setText(message.getSender());
        holder.tvMessage.setText(message.getText());
        holder.tvTimestamp.setText(TimeUtils.formatTime(message.getTimestamp()));

        // Style differently for user vs pet messages
        if ("User".equals(message.getSender())) {
            holder.itemView.setBackgroundResource(R.drawable.bg_user_message);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_pet_message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // THIS CLASS MUST BE HERE! ↓↓↓
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvMessage;
        TextView tvTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}