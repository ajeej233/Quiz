package com.staraj.quiza;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.staraj.quiza.databinding.FragmentWalletBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


public class WalletFragment extends Fragment {




    public WalletFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    FragmentWalletBinding binding;

    FirebaseFirestore database;
    int coinR = -50000;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater,container,false);
        database = FirebaseFirestore.getInstance();



        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

                binding.currentCoins.setText(String.valueOf(user.getCoins()));



            }
        });



        binding.sendRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(binding.emailBox.getText().toString().isEmpty()){
                    binding.emailBox.setError("Paytm number is require");
                }else if(user.getCoins() < 50000){
                    Toast.makeText(getContext(), "You need 50000 coins to get withdraw.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String uid = FirebaseAuth.getInstance().getUid();
                    String payPal = binding.emailBox.getText().toString();
                    WithdrawRequest request =  new WithdrawRequest(uid,payPal,user.getName());
                    database
                            .collection("Withdraws")
                            .document(uid)
                            .set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Request Send Successfully", Toast.LENGTH_SHORT).show();
                            database.collection("users")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .update("coins", FieldValue.increment(coinR));

                        }
                    });

                }

            }
        });

        return binding.getRoot();
    }

}