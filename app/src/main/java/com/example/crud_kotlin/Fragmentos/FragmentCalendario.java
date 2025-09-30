package com.example.crud_kotlin.Fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud_kotlin.Modelos.Recordatorio;
import com.example.crud_kotlin.R;
import com.example.crud_kotlin.adapter.RecordatorioAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FragmentCalendario extends Fragment {

    private RecyclerView rvRecordatorios;
    private RecordatorioAdapter adapter;
    private List<Recordatorio> listaRecordatorios = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        rvRecordatorios = view.findViewById(R.id.recyclerViewCalendario);
        adapter = new RecordatorioAdapter(getContext(), listaRecordatorios);
        rvRecordatorios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecordatorios.setAdapter(adapter);

        // Cargar recordatorios en tiempo real
        cargarRecordatorios();

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            // Navegar al fragment de agregar recordatorio
            FragmentCalendaryAdd fragment = new FragmentCalendaryAdd();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentoFL, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void cargarRecordatorios() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            Log.e("FirestoreDebug", "Usuario no autenticado");
            return;
        }

        String uid = usuario.getUid();
        Log.d("FirestoreDebug", "UID del usuario: " + uid);

        db.collection("recordatorios")
                .whereEqualTo("idUsuario", uid) // coincide con Firestore
                .orderBy("fecha") // opcional, recuerda crear índice si sale error
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error: " + e.getMessage());
                        return;
                    }
                    listaRecordatorios.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Log.d("FirestoreDebug", "Doc: " + doc.getData()); // Para verificar
                        Recordatorio r = doc.toObject(Recordatorio.class);
                        listaRecordatorios.add(r);
                    }
                    adapter.notifyDataSetChanged();
                });

    }
}
