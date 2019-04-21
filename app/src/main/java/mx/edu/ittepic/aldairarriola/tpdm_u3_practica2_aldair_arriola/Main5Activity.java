package mx.edu.ittepic.aldairarriola.tpdm_u3_practica2_aldair_arriola;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Main5Activity extends AppCompatActivity {
    public EditText fundador, nombre, origen;
    public Button s,u;
    public FirebaseFirestore DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        fundador = findViewById(R.id.actmarcafundador);
        nombre = findViewById(R.id.actmarcaname);
        origen = findViewById(R.id.actmarcapais);

        s = findViewById(R.id.actmarcabuscar);
        u = findViewById(R.id.actmarcaboton);
        DB = FirebaseFirestore.getInstance();

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar(nombre.getText().toString());
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
        Marca car = new Marca(nombre.getText().toString(), fundador.getText().toString(),
                origen.getText().toString());

        DB.collection("Marcas")
                .document(nombre.getText().toString())
                .set(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main5Activity.this, "SE ACTUALIZO CORRECTAMENTE",
                                Toast.LENGTH_SHORT).show();
                        fundador.setText("");nombre.setText("");origen.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main5Activity.this,
                                "ERROR NO SE PUDO INSERTAR", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void buscar(String idABuscar){
        DB.collection("Marcas")
                .whereEqualTo("nombre",idABuscar)
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

                                        fundador.setText(dato.get("fundador").toString());
                                        origen.setText(dato.get("paisorigen").toString());

                                    }
                                }else{
                                    Toast.makeText(Main5Activity.this,
                                            "NO SE ENCONTRO COINCIDENCIA!",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });
    }



}
