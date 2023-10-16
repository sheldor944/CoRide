package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public MessageAdapter(Context context, ArrayList<Message> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_from_left);
            view.startAnimation(animation);
            return new senderVierwHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_from_left);
            view.startAnimation(animation);
            return new reciverViewHolder(view);
        }
    }

    public static boolean containsLinks(String text) {
        String URL_REGEX = "(https?://)?(www\\.)?([a-zA-Z0-9]+\\.[a-zA-Z]{2,6}(\\.[a-zA-Z]{2,6})?)";
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static List<String> extractLinks(String text) {
        String URL_REGEX = "(https?://)?(www\\.)?([a-zA-Z0-9]+\\.[a-zA-Z]{2,6}(\\.[a-zA-Z]{2,6})?)";
        List<String> links = new ArrayList<>();
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String link = matcher.group();
            links.add(link);
        }
        return links;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder , int position) {
        Message message = messagesAdpterArrayList.get(position);
        String messageText = message.getText();

        // Use a Pattern to find URLs in the message text
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(messageText);
        SpannableString spannableString = new SpannableString(messageText);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Add an UnderlineSpan to the matched URL
            spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // Set the modified SpannableString to the appropriate TextView in the holder
        if (holder instanceof senderVierwHolder) {
            ((senderVierwHolder) holder).msgtxt.setText(spannableString);
        } else if (holder instanceof reciverViewHolder) {
            ((reciverViewHolder) holder).msgtxt.setText(spannableString);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messagesAdpterArrayList.get(position).getText();
//                if (containsLinks(msg)) {
//                    List<String> ls = extractLinks(msg);
//                    Intent intent = new Intent(context, WebviewActivity.class);
//                    intent.putExtra("URL", ls.get(0));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        if (holder.getClass()==senderVierwHolder.class){
            senderVierwHolder viewHolder = (senderVierwHolder) holder;
            viewHolder.msgtxt.setText(message.getText());
        }else { reciverViewHolder viewHolder = (reciverViewHolder) holder;
            viewHolder.msgtxt.setText(message.getText());
        }
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    class  senderVierwHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        public senderVierwHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
            Linkify.addLinks(msgtxt, Linkify.WEB_URLS);
        }
    }
    class reciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);
            Linkify.addLinks(msgtxt, Linkify.WEB_URLS);
        }
    }
}