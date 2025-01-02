package com.example.connecttojson;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // buat variable volley untuk mengirim request ke json
    RequestQueue requestQueue;
    String base_url="http://192.168.18.93/belajar_api/costumer.php";
    RecyclerView rvPelanggan; // RecyclerView untuk menampilkan data pelanggan
    AdapterPelanggan adapterPelanggan; // Adapter untuk RecyclerView
    ArrayList<ModelPelanggan> list; // List untuk menyimpan data pelanggan
    ProgressDialog progressDialog; // Untuk menampilkan loading dialog
    AlphaAnimation btnAnimasi = new AlphaAnimation(1F, 0.5F); // Animasi tombol

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setView();
        requestQueue = Volley.newRequestQueue(this);
        getData();
    }

    private void setView() {
        progressDialog = new ProgressDialog(this); // Inisialisasi ProgressDialog
        rvPelanggan = findViewById(R.id.rvPelanggan); // Temukan RecyclerView dari layout
        list = new ArrayList<>(); // Inisialisasi list untuk menampung data pelanggan

        // Inisialisasi LinearLayoutManager untuk RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL); // Mengatur orientasi menjadi vertikal
        rvPelanggan.setHasFixedSize(true); // Optimalkan ukuran RecyclerView
        rvPelanggan.setLayoutManager(llm); // Set layout manager ke RecyclerView
    }

    private void showMsg() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Informasi");
            progressDialog.setMessage("Loading Data...");
            progressDialog.setCancelable(false); // Tidak bisa dibatalkan dengan menekan di luar
        }
        progressDialog.show(); // Tampilkan dialog
    }

    private void getData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, base_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if(status.equalsIgnoreCase("true")){
                        // ambil json array dari object data
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int a=0; a<jsonArray.length(); a++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(a);
                            ModelPelanggan mp = new ModelPelanggan();
                            mp.setId(jsonObject1.getString("id"));
                            mp.setNama(jsonObject1.getString("nama"));
                            mp.setEmail(jsonObject1.getString("alamat"));
                            mp.setHp(jsonObject1.getString("hp"));
                            list.add(mp);
                        }
                        adapterPelanggan = new AdapterPelanggan(MainActivity.this, list);
                        adapterPelanggan.notifyDataSetChanged();
                        rvPelanggan.setAdapter(adapterPelanggan);
                        Toast.makeText(MainActivity.this, ""+jsonArray.toString(), Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
    });
       requestQueue.add(jsonObjectRequest);
    }
}