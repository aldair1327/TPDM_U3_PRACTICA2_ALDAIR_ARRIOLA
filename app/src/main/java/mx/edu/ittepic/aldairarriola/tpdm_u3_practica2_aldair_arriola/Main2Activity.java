package mx.edu.ittepic.aldairarriola.tpdm_u3_practica2_aldair_arriola;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

public class Main2Activity extends AppCompatActivity {
    public EditText id, nombre, year;
    public Button insertar,eliminar,consultar, actualizar;
    public ListView lista;
    public String con;
    public FirebaseFirestore DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        id = findViewById(R.id.idauto);
        nombre = findViewById(R.id.nombreauto);
        year = findViewById(R.id.modeloauto);

        insertar = findViewById(R.id.autoinsertar);
        eliminar = findViewById(R.id.autoeliminar);
        consultar = findViewById(R.id.autoconsultar);
        actualizar = findViewById(R.id.autoactualizar);

        con = "";

        DB = FirebaseFirestore.getInstance();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarAuto();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarAuto();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consultarTodos();
                consultarAuto();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, Main4Activity.class));
            }
        });
    }


    private void consultarAuto(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText porid = new EditText(this);
        porid.setInputType(InputType.TYPE_CLASS_TEXT);
        porid.setHint("ID a buscar");

        alerta.setTitle("BUSQUEDA").setMessage("ESCRIBA ID DEL COCHE")
                .setView(porid)
                .setPositiveButton("buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(porid.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this,
                                    "DEBES PONER UN ID", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        consultarAuto(porid.getText().toString());
                    }
                })
                .setNegativeButton("cancelar",null)
                .show();
    }


    private void consultarAuto(String idABuscar){
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
                                    Toast.makeText(Main2Activity.this,
                                            "NO SE ENCONTRO COINCIDENCIA!",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });
    }



    private void eliminarAuto(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText idEliminar = new EditText(this);
        idEliminar.setHint("NO DEBE QUEDAR VACIO");

        alerta.setTitle("ATENCION").setMessage("ESCRIBA EL ID:")
                .setView(idEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(idEliminar.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this, "EL ID ESTA VACIO",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        eliminarAutoOficial(idEliminar.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    private void eliminarAutoOficial(String idEliminar){
        DB.collection("Automoviles")
                .document(idEliminar)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main2Activity.this,
                                "SE ELIMINO!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this,
                                "NO SE ENCONTRO COINCIDENCIA!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void insertarAuto(){
        Auto car = new Auto(id.getText().toString(), nombre.getText().toString(),
                year.getText().toString());

        DB.collection("Automoviles")
                .document(id.getText().toString())
                .set(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main2Activity.this, "SE INSERTO CORRECTAMENTE",
                                Toast.LENGTH_SHORT).show();
                        id.setText("");nombre.setText("");year.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this,
                                "ERROR NO SE PUDO INSERTAR", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void consultarTodos(){
        DB.collection("Automoviles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        con = "";
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot registro : task.getResult()){
                                Map<String, Object> datos = registro.getData();

                                con+=" -- "+datos.get("nombre").toString();

                            }
                        } else {
                            Toast.makeText(Main2Activity.this,
                                    "NO DATOS", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Main2Activity.this,
                                ""+con, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
