package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    String email ;
    String name ;
    String phone;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        FirebaseAuth firebaseAuth ;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String displayName = user.getDisplayName();
        EditText editText  ;
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        Intent intent = getIntent();

        // Retrieve the "name" extra from the fragment's arguments bundle
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("name")) {
            String name = arguments.getString("name");

            // Find the TextView within the inflated layout and set the name
            TextView nameText = binding.nameText; // Assuming your TextView ID is 'nameText' in your layout
            nameText.setText(name);
        }else{
//            TextView nameText = binding.nameText; // Assuming your TextView ID is 'nameText' in your layout
//            nameText.setText("kitare");
        }

        // Rest of your code

        return root;
    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}