package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.UserModel;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    String email ;
    String name ;
    String phone;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d("entered" , "notificationFragment o dukse ");
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            Log.d("user" , "user is null ");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());
        System.out.println(user.getUid());
        Log.d("entered2" , "notificationFragment o dukse 2 ");



        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                Log.d("entered3" , "notificationFragment o dukse3 ");

                if (task.isSuccessful()) {
                    Log.d("entered succesfull 4" , "successful o dukse 4 ");


                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Get the user's data from the DocumentSnapshot
                        Log.d("entered5" , "document exists ");

                        String name = documentSnapshot.getString("firstName");
                        System.out.println(name);
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");
//                        String password = documentSnapshot.getString("password");

                        TextView nameText = (root.findViewById(R.id.nameText));
                        nameText.setText(name + lastName);

                        TextView emailText = (root.findViewById(R.id.textEmail));
                        emailText.setText(email);

                        TextView phoneNumber = root.findViewById(R.id.textPhone);
                        phoneNumber.setText(phone);

//                        Log.d("print" ,name + lastName + email + phone + password  );

                        // Do something with the user's data
                    } else {
                        // The user's document does not exist
                        Log.w("document " , " document not found ");
                    }
                } else {
                    // An error occurred while getting the user's data
                }
            }
        });


//        Intent intent = getIntent();

        // Rest of your code

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}