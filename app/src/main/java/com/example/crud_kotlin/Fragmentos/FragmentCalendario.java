package com.example.crud_kotlin.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_kotlin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FragmentCalendario extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);



        FloatingActionButton fab = view.findViewById(R.id.fab_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acción al hacer clic en el botón flotante
                Toast.makeText(getActivity(), "Botón flotante presionado", Toast.LENGTH_SHORT).show();
            }
        });



        return view;

    }




}