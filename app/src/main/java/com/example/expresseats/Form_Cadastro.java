package com.example.expresseats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form_Cadastro extends AppCompatActivity {
    private CircleImageView photoUser;
    private Button btSelectPhoto, btRegister;
    private EditText editName,editEmail,editePassword;
    private TextView msgError;

    private Uri mSelectUri;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InciarCompronentes();
        //passando nosso sistema de verificar os itens preenchidos para os itens que escolhemos
        editName.addTextChangedListener(cadastroTextWatcher);
        editEmail.addTextChangedListener(cadastroTextWatcher);
        editePassword.addTextChangedListener(cadastroTextWatcher);

        btRegister.setOnClickListener(v -> cadastrarUsuaio(v));
        //criando botao para selecionar imagem do usuario
        btSelectPhoto.setOnClickListener(v -> selectPhotoGalery());
    }
    //Metodo para cadastrar novos usuarios baseado em email e senha. Aqui recuperamos email e senha
    public void cadastrarUsuaio(View view){
        String email = editEmail.getText().toString();
        String password = editePassword.getText().toString();
//conectamos nosso app com o firebase para poder enviar os dados para o banco de dados
        //caso o cadastro seja bem sucedido enviara uma mensagem para o usuario com um botão para finalizar a pagina atual
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                SaveDataUsers();
                //uma barra que podemos personalizar mais livremente e criamos um botão dentro dessa barra
                Snackbar snackbar = Snackbar.make(view,"Cadastro realizado com sucesso",Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v -> finish());
                snackbar.show();
            }
        });
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        mSelectUri = data.getData();

                        try {
                            photoUser.setImageURI(mSelectUri);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    public void selectPhotoGalery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    public void SaveDataUsers(){
        //randomiza os nomes salvos no banco
        String nomeArquivo = UUID.randomUUID().toString();
        final StorageReference reference = FirebaseStorage.getInstance().getReference("/image/"+nomeArquivo);
        reference.putFile(mSelectUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String foto = uri.toString();
                                //Iniciar o banco de dados - FireStore
                                String nome = editName.getText().toString();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String,Object> usuarios = new HashMap<>();
                                usuarios.put("nome",nome);
                                usuarios.put("foto",foto);

                                usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
                                documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void InciarCompronentes(){
        photoUser = findViewById(R.id.photoUser);
        btSelectPhoto = findViewById(R.id.selectPhoto);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editePassword = findViewById(R.id.editPassword);
        msgError = findViewById(R.id.messageError);
        btRegister = findViewById(R.id.btRegister);
    }

    //Um sistema para verificar se os itens estão sendo preenchidos
    TextWatcher cadastroTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = editName.getText().toString();
            String email = editEmail.getText().toString();
            String password = editePassword.getText().toString();
//caso o itens estiverem preenchidos o botão será liberado para clicar e trocará a cor para vermelho
            if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                btRegister.setEnabled(true);
                btRegister.setBackgroundColor(getResources().getColor(R.color.dark_red));
//caso não o botão continuará desabilitado e permanecerá a cor cinza
            }else{
                btRegister.setEnabled(false);
                btRegister.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}