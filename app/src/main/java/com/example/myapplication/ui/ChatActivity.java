package com.example.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.Message;
import com.example.myapplication.helper.GetUserNameCallback;
import com.example.myapplication.service.LocationDB;
import com.example.myapplication.service.MessageAdapter;
import com.example.myapplication.service.testerActivity;
import com.example.myapplication.ui.map.RideDetailsOnMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends testerActivity {
    private static final String TAG = "ChatActivity";
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

    private String mUserId;
    private String mPassengerId;
    private String mPassengerStartLocation;
    private String mPassengerEndLocation;

    private String mRiderId;
    private String mRiderStartLocation;
    private String mRiderEndLocation;

    private boolean stopThreads = false;
    private boolean isNotOnThisActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getInformationFromIntent();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mUserId = auth.getUid();
        isNotOnThisActivity = false;

        // data fetching system should be implemented
        // TODO: 10/22/2023
        //receiver id should come from database or something
//        receiverUID = getIntent().getStringExtra("UID");

        receiverName = "";
        receiverUID = null;
        if(!mPassengerId.equals(mUserId)) receiverUID = mPassengerId;
        else receiverUID = mRiderId;

        LocationDB locationDB = new LocationDB();
        locationDB.getUserName(receiverUID, new GetUserNameCallback() {
            @Override
            public void onUserNameRecieved(String name, String phone) {
                if(isNotOnThisActivity) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        receiverName = name;
                        receiver.setText(name);
                    }
                });
            }
        });

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

        mMapIcon = findViewById(R.id.map_image);
        mMapIcon.setOnClickListener(view -> {
            isNotOnThisActivity = true;
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            Intent intent = new Intent(this, RideDetailsOnMapActivity.class);

            intent.putExtra("passenger_id", mPassengerId);
            intent.putExtra("passenger_start_location", mPassengerStartLocation);
            intent.putExtra("passenger_end_location", mPassengerEndLocation);

            intent.putExtra("rider_id", mRiderId);
            intent.putExtra("rider_start_location", mRiderStartLocation);
            intent.putExtra("rider_end_location", mRiderEndLocation);

            startActivity(intent);
            progressDialog.dismiss();
        });
    }

    private void getInformationFromIntent() {
        Intent intent = getIntent();
        mPassengerId = intent.getStringExtra("passenger_id");
        mPassengerStartLocation = intent.getStringExtra("passenger_start_location");
        mPassengerEndLocation = intent.getStringExtra("passenger_end_location");

        mRiderId = intent.getStringExtra("rider_id");
        mRiderStartLocation = intent.getStringExtra("rider_start_location");
        mRiderEndLocation = intent.getStringExtra("rider_end_location");

        Log.d(TAG, "getInformationFromIntent: passenger: " + mPassengerId + " "
                + mPassengerStartLocation + " " + mPassengerEndLocation);
        Log.d(TAG, "getInformationFromIntent: rider: " + mRiderId + " "
                + mRiderStartLocation + " " + mRiderEndLocation);
    }

    @Override
    public void onBackPressed() {

    }
}