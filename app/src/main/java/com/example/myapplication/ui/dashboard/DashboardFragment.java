package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;

import java.util.List;

public class DashboardFragment extends Fragment {
    View root;

    private FragmentDashboardBinding binding;
    private Button riderHisotory;
    private Button passengerHisotry;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        riderHisotory = root.findViewById(R.id.riderHistory);
        passengerHisotry = root.findViewById(R.id.passengerHistory);

        riderHisotory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , RiderHistoryActivity.class);
                startActivity(intent);
            }
        });

        passengerHisotry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , PassengerHisotryActivity.class);
                startActivity(intent);
            }
        });


//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//
//        Button button = (Button) root.findViewById(R.id.button5);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                String emailAddress = "recipient@example.com";
////
////
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the email");
//                intent.putExtra(Intent.EXTRA_TEXT, "Body of the email");
//
//                PackageManager packageManager = requireActivity().getPackageManager();
//                List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
//
//                if (resolveInfoList.size() > 0) {
//                    // Gmail app is available, start the activity
//                    startActivity(intent);
//                } else {
//                    // Gmail app is not available, handle this case
//                    Toast.makeText(requireContext(), "Gmail app is not installed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}