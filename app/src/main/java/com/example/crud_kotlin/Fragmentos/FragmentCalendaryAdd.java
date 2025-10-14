package com.example.crud_kotlin.Fragmentos;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.crud_kotlin.Modelos.Recordatorio;
import com.example.crud_kotlin.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class FragmentCalendaryAdd extends Fragment {


    private MaterialButton btnViewDetail;
    private EditText etTitulo,etlocation;
    private TextView etHora, etFecha;
    private Button btnGuardar;
    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendary_add, container, false);

        etTitulo = view.findViewById(R.id.et_event_title);
        etHora = view.findViewById(R.id.textViewHora);
        etFecha = view.findViewById(R.id.textViewFecha);
        etlocation = view.findViewById(R.id.et_location);
        btnGuardar = view.findViewById(R.id.btn_save);

        db = FirebaseFirestore.getInstance();

        btnGuardar.setOnClickListener(v -> guardarRecordatorio());

        //Funciones de TimePicker
        TextView textViewHora = view.findViewById(R.id.textViewHora); // o findViewById si estás en Activity

        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        String amPm;
                        int hourFormatted;

                        if (selectedHour >= 12) {
                            amPm = "PM";
                            hourFormatted = selectedHour == 12 ? 12 : selectedHour - 12;
                        } else {
                            amPm = "AM";
                            hourFormatted = selectedHour == 0 ? 12 : selectedHour;
                        }

                        String horaSeleccionada = String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm);
                        textViewHora.setText(horaSeleccionada);
                    }
                },
                hora,
                minuto,
                false
                );


        //funcion para metodo de fecha

        etFecha = view.findViewById(R.id.textViewFecha); // o findViewById si estás en Activity


        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(), // o "this" si estás en una Activity
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // El mes empieza en 0, así que sumamos 1
                        String fechaSeleccionada = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etFecha.setText(fechaSeleccionada);
                    }
                },
                año,
                mes,
                dia
        );





        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        textViewHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });





        return view;
    }

    private void guardarRecordatorio() {
        String titulo = etTitulo.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String lugar = etlocation.getText().toString().trim();

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.isEmpty() || hora.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(getContext(), "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Recordatorio recordatorio = new Recordatorio(titulo, hora, fecha, usuario.getUid(),lugar);

        db.collection("recordatorios")
                .add(recordatorio)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Recordatorio guardado", Toast.LENGTH_SHORT).show();
                    etTitulo.setText("");
                    etHora.setText("");
                    etFecha.setText("");
                    etlocation.setText("");
                    // Volver al fragmento principal
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", e.getMessage());
                });
    }
}
