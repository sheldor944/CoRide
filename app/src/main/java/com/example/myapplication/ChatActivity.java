package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Message;
import com.example.myapplication.ui.map.RideDetailsOnMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    String receiverUID, receiverName, SenderUID;
    TextView receiver;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CardView sendButton;
    EditText textMessage;

    String senderRoom, receiverRoom;
    RecyclerView recycleView;
    ArrayList<Message> messagesArrayList;
    MessageAdapter msgAdapter;

    private CircleImageView mMapIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMapIcon = findViewById(R.id.map_image);
        mMapIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, RideDetailsOnMapActivity.class);
            startActivity(intent);
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        // data fetching system should be implemented
        // TODO: 10/22/2023
        //receiver id should come from database or something
        receiverUID = getIntent().getStringExtra("UID");

        receiverName = "messi";
//        receiverUID = "ItIDT7jlUDOMRheIs4fif8DTc0A2";

        messagesArrayList = new ArrayList<>();

        sendButton = findViewById(R.id.sendbtnn);
        textMessage = findViewById(R.id.textmsg);
        receiver = findViewById(R.id.recivername);

        recycleView = findViewById(R.id.msgadpter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);

        recycleView.setLayoutManager(linearLayoutManager);
        msgAdapter = new MessageAdapter(ChatActivity.this, messagesArrayList);
        recycleView.setAdapter(msgAdapter);

        receiver.setText(receiverName);

        SenderUID = auth.getUid();

        senderRoom = SenderUID + receiverUID;
        receiverRoom = receiverUID + SenderUID;

        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

//        int newPosition = msgAdapter.getItemCount() - 1;
//        recycleView.scrollToPosition(newPosition);
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message messages = dataSnapshot.getValue(Message.class);
                    messagesArrayList.add(messages);
                }
                msgAdapter.notifyDataSetChanged();
                int newPosition = msgAdapter.getItemCount();
                recycleView.smoothScrollToPosition(newPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int heightDiff = oldBottom - bottom;
                if (heightDiff > 200) {
                    int keyboardHeight = heightDiff;
                    recycleView.smoothScrollBy(0, keyboardHeight);
                }
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }

                textMessage.setText("");
//                sendNotification(message);
                Date date = new Date();
                Message messagess = new Message(SenderUID, receiverUID , message , date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });

    }



}