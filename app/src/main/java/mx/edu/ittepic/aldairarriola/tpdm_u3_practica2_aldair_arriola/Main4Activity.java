package mx.edu.ittepic.aldairarriola.tpdm_u3_practica2_aldair_arriola;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class Main4Activity extends AppCompatActivity {
    public EditText id, nombre, year;
    public Button s,u;
    public FirebaseFirestore DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        id = findViewById(R.id.actautoid);
        nombre = findViewById(R.id.actautoname);
        year = findViewById(R.id.actautoaño);

        s = findViewById(R.id.actautobuscar);
        u = findViewById(R.id.actautoboton);

        DB = FirebaseFirestore.getInstance();

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar(id.getText().toString());
            }
        });

        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar();
            }
        });

    }

    private void actualizar(){
        Auto car = new Auto(id.getText().toString(), nombre.getText().toString(),
                year.getText().toString());

        DB.collection("Automoviles")
                .document(id.getText().toString())
                .set(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main4Activity.this, "SE ACTUALIZO CORRECTAMENTE",
                                Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");year.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main4Activity.this,
                                "ERROR NO SE PUDO INSERTAR", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void buscar(String idABuscar){
        DB.collection("Automoviles")
                .whereEqualTo("id",idABuscar)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        Query q = queryDocumentSnapshots.getQuery();

                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot registro : task.getResult()){
                                        Map<String, Object> dato = registro.getData();

                                        nombre.setText(dato.get("nombre").toString());
                                        year.setText(dato.get("year").toString());

                                    }
                                }else{
                                    Toast.makeText(Main4Activity.this,
                                            "NO SE ENCONTRO COINCIDENCIA!",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });
    }


}
