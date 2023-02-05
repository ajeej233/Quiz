package com.staraj.quiza;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.staraj.quiza.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FragmentHomeBinding binding;
    FirebaseFirestore database;
    Button Watch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        final ArrayList<CategoryModel> categories = new ArrayList<>();
        binding.Watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),VideoActivity.class);
                startActivity(intent);
            }
        });


        final CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        database.collection("Categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            CategoryModel model = snapshot.toObject(CategoryModel.class);
                            model.setCategoryId(snapshot.getId());
                            categories.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoryList.setAdapter(adapter);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    

}