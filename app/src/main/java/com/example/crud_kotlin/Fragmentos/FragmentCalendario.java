package com.example.crud_kotlin.Fragmentos;

import static com.example.crud_kotlin.R.*;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    // TextView que se muestra cuando no hay datos
    private TextView txtVacio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        rvRecordatorios = view.findViewById(R.id.recyclerViewCalendario);
        txtVacio = view.findViewById(R.id.txtVacio); // debe existir en tu XML

        adapter = new RecordatorioAdapter(getContext(), listaRecordatorios);
        rvRecordatorios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecordatorios.setAdapter(adapter);

        // Cargar recordatorios en tiempo real
        cargarRecordatorios();

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
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
        if (usuario == null) return;

        String uid = usuario.getUid();

        db.collection("recordatorios")
                .whereEqualTo("idUsuario", uid)
                .orderBy("fecha")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) return;

                    listaRecordatorios.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Recordatorio r = doc.toObject(Recordatorio.class);
                        Log.d("RecordatorioDebug", "Titulo: " + r.getTitulo() + ", Fecha: " + r.getFecha() + ", Hora: " + r.getHora());

                        if (r != null) listaRecordatorios.add(r);

                    }

                    adapter.notifyDataSetChanged();

                    // Mostrar mensaje si la lista está vacía
                    TextView tvEmpty = getView().findViewById(id.txtVacio);
                    if (listaRecordatorios.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                    }
                });
    }





}
