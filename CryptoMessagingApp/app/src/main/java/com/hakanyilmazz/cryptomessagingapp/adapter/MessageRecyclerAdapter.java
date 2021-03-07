package com.hakanyilmazz.cryptomessagingapp.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakanyilmazz.cryptomessagingapp.R;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageViewHolder> {

    private static int colorCount = 0;

    private ArrayList<String> emailList;
    private ArrayList<String> messageList;

    public MessageRecyclerAdapter(ArrayList<String> emailList, ArrayList<String> messageList) {
        this.emailList = emailList;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_message_row, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.emailText.setText(emailList.get(position));
        holder.messageText.setText(messageList.get(position));

        holder.linearLayout.setBackgroundColor(getRandomColor(holder));
    }

    private int getRandomColor(@NonNull MessageViewHolder holder) {
        Resources resources = holder.linearLayout.getResources();
        int colorId = Integer.MIN_VALUE;

        switch (colorCount) {
            case 0:
                colorId = resources.getColor(R.color.gray_50);
                break;
            case 1:
                colorId = resources.getColor(R.color.blue_50);
                break;
            case 2:
                colorId = resources.getColor(R.color.gray_100);
                break;
            case 3:
                colorId = resources.getColor(R.color.orange_50);
                break;
            default:
                colorId = resources.getColor(R.color.white);
                break;
        }

        colorCount++;
        if (colorCount == 4) {
            colorCount = 0;
        }

        return colorId;
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView emailText;
        TextView messageText;

        LinearLayout linearLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            emailText    = itemView.findViewById(R.id.recyclerView_emailText);
            messageText  = itemView.findViewById(R.id.recyclerView_messageText);
            linearLayout = itemView.findViewById(R.id.recyclerView_linearLayout);
        }
    }

}
