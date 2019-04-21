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

public class Main3Activity extends AppCompatActivity {
    public EditText nombre, fundador,origen;
    public Button insertar,eliminar,consultar, actualizar;
    public String con;
    public FirebaseFirestore DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        nombre = findViewById(R.id.nombrmarca);
        fundador = findViewById(R.id.fundador);
        origen = findViewById(R.id.paisorigen);

        insertar = findViewById(R.id.marcainsertar);
        eliminar = findViewById(R.id.marcaeliminar);
        consultar = findViewById(R.id.marcaconsultar);
        actualizar = findViewById(R.id.marcactualizar);
        con = "";
        DB = FirebaseFirestore.getInstance();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarMarca();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarMarca();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consultarTodos();
                consultarMarca();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main3Activity.this, Main5Activity.class));
            }
        });


    }

    private void eliminarMarca(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText idEliminar = new EditText(this);
        idEliminar.setHint("NO DEBE QUEDAR VACIO");

        alerta.setTitle("ATENCION").setMessage("ESCRIBA EL NOMBRE:")
                .setView(idEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(idEliminar.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this, "EL ID ESTA VACIO",
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
        DB.collection("Marcas")
                .document(idEliminar)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this,
                                "SE ELIMINO!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this,
                                "NO SE ENCONTRO COINCIDENCIA!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void insertarMarca(){
        Marca car = new Marca(nombre.getText().toString(), fundador.getText().toString(),
                origen.getText().toString());

        DB.collection("Marcas")
                .document(nombre.getText().toString())
                .set(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this, "SE INSERTO CORRECTAMENTE",
                                Toast.LENGTH_SHORT).show();
                        fundador.setText("");nombre.setText("");origen.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this,
                                "ERROR NO SE PUDO INSERTAR", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void consultarTodos(){
        DB.collection("Marcas")
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
                            Toast.makeText(Main3Activity.this,
                                    "NO DATOS", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Main3Activity.this,
                                ""+con, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void consultarMarca(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText porid = new EditText(this);
        porid.setInputType(InputType.TYPE_CLASS_TEXT);
        porid.setHint("ID a buscar");

        alerta.setTitle("BUSQUEDA").setMessage("ESCRIBA EL NOMBRE DE LA MARCA")
                .setView(porid)
                .setPositiveButton("buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(porid.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this,
                                    "DEBES PONER UN ID", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        consultarMarca(porid.getText().toString());
                    }
                })
                .setNegativeButton("cancelar",null)
                .show();
    }

    private void consultarMarca(String idABuscar){
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


                                        nombre.setText(dato.get("nombre").toString());
                                        fundador.setText(dato.get("fundador").toString());
                                        origen.setText(dato.get("paisorigen").toString());
                                    }
                                }else{
                                    Toast.makeText(Main3Activity.this,
                                            "NO SE ENCONTRO COINCIDENCIA!",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });
    }


}
