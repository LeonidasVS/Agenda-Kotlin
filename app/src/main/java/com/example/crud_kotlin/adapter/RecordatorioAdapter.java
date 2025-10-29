package com.example.crud_kotlin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud_kotlin.Modelos.Recordatorio;
import com.example.crud_kotlin.R;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordatorioAdapter extends RecyclerView.Adapter<RecordatorioAdapter.RecordatorioViewHolder> {

    private List<Recordatorio> lista;
    private Context context;

    public RecordatorioAdapter(Context context, List<Recordatorio> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public RecordatorioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_calendary, parent, false);
        return new RecordatorioViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecordatorioViewHolder holder, int position) {
        Recordatorio r = lista.get(position);
        if (r == null) return;

        // ---------------- FECHA ----------------
        FechaProcesada fp = new FechaProcesada();
         Log.d("RecordatorioAdapter", "Procesando fecha: " + r.getFecha());
         Log.d("Recordatorio hora","Procesando hora: " + r.getHora());

        if (r.getFecha() != null && !r.getFecha().isEmpty()) {
            fp = procesarFecha(r.getFecha()); // Solo parsea "dd/MM/yyyy"
        } else {
            fp.dayOfWeek = "";
            fp.dayNumber = "";
            fp.month = "";
            fp.year = "";
        }

        holder.tvDay.setText(fp.dayNumber);
        holder.tvMonth.setText(fp.month);
        holder.tvYear.setText(fp.year);
        holder.tvDayOfWeek.setText(fp.dayOfWeek);
        holder.tvLocation.setText(r.getLugar() != null ? r.getLugar() : "");



        // ---------------- HORA ----------------
        holder.tvHora.setText(r.getHora() != null ? r.getHora() : "");

        // ---------------- TITULO ----------------
        holder.tvTitulo.setText(r.getTitulo() != null ? r.getTitulo() : "Sin título");

        // ---------------- DETALLES ----------------
        holder.btnViewDetails.setOnClickListener(v ->
                Toast.makeText(
                        context,
                        "Detalle: " + (r.getTitulo() != null ? r.getTitulo() : "Sin título"),
                        Toast.LENGTH_SHORT
                ).show()
        );
    }





    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class RecordatorioViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvHora, tvDay, tvMonth, tvYear, tvDayOfWeek,tvLocation;
        MaterialButton btnViewDetails;

        public RecordatorioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_event_title);
            tvHora = itemView.findViewById(R.id.tv_time);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvLocation = itemView.findViewById(R.id.tv_location);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }

    private FechaProcesada procesarFecha(String fechaTexto) {
        FechaProcesada fp = new FechaProcesada();

        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaTexto);

            fp.dayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES")).format(fecha);
            fp.dayNumber = new SimpleDateFormat("dd", Locale.getDefault()).format(fecha);
            fp.month = new SimpleDateFormat("MMM", new Locale("es", "ES")).format(fecha);
            fp.year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(fecha);

        } catch (Exception e) {
            Log.e("RecordatorioAdapter", "Error procesando fecha: " + fechaTexto, e);

            fp.dayOfWeek = "???";
            fp.dayNumber = "--";
            fp.month = "---";
            fp.year = "----";
        }

        return fp;
    }



    // Clase auxiliar para manejar la fecha procesada
    static class FechaProcesada {
        String dayOfWeek; // Ej: Lunes
        String dayNumber; // Ej: 30
        String month;     // Ej: Sep
        String year;      // Ej: 2025
    }
}
