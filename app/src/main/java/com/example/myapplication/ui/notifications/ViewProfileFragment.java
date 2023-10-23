package com.example.myapplication.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MyFirebaseMessagingService;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private static final String TAG = "NotificationsFragment";

    String email ;
    String name ;
    String phone;
    ImageView imageView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: inside notifications fragment");
        MyFirebaseMessagingService m = new MyFirebaseMessagingService();
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            Log.d(TAG, "onCreateView: user is null");
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

//        Button upload = root.findViewById(R.id.upload);
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
//                }
//
//                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                int PICK_PHOTO_REQUEST = 1234;
//                startActivityForResult(pickPhotoIntent, PICK_PHOTO_REQUEST);
//            }
//        });


//        Intent intent = getIntent();

        // Rest of your code

//        imageView = root.findViewById(R.id.imageView);

        return root;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == getActivity().RESULT_OK) {
//            if (requestCode == 1234) { // This is the PICK_PHOTO_REQUEST
//                Uri selectedImage = data.getData();
//
//                try {
//                    // Convert the Uri to a Bitmap
//                    Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//
//                    // Define the desired width and height. For this example, I'm using the width and height of the ImageView.
//                    int imageViewWidth = imageView.getWidth();
//                    int imageViewHeight = imageView.getHeight();
//
//                    float originalBitmapAspectRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();
//                    float imageViewAspectRatio = (float) imageViewWidth / imageViewHeight;
//
//                    int scaledWidth, scaledHeight;
//
//                    if (originalBitmapAspectRatio > imageViewAspectRatio) {
//                        // Original image is wider relative to the ImageView
//                        scaledWidth = imageViewWidth;
//                        scaledHeight = (int) (imageViewWidth / originalBitmapAspectRatio);
//                    } else {
//                        // Original image is taller relative to the ImageView
//                        scaledHeight = imageViewHeight;
//                        scaledWidth = (int) (imageViewHeight * originalBitmapAspectRatio);
//                    }
//                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
//
//                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                    imageView.setImageBitmap(scaledBitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    // Handle the exception
//                }
//            }
//
//        }
//    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}