package com.example.expresseats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form_Cadastro extends AppCompatActivity {
    private CircleImageView photoUser;
    private Button btSelectPhoto, btRegister;
    private EditText editName,editEmail,editePassword;
    private TextView msgError;

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

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuaio(v);
            }
        });
    }
    //Metodo para cadastrar novos usuarios baseado em email e senha. Aqui recuperamos email e senha
    public void cadastrarUsuaio(View view){
        String email = editEmail.getText().toString();
        String password = editePassword.getText().toString();
//conectamos nosso app com o firebase para poder enviar os dados para o banco de dados
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            //caso o cadastro seja bem sucedido enviara uma mensagem para o usuario com um botão para finalizar a pagina atual
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //uma barra que podemos personalizar mais livremente e criamos um botão dentro dessa barra
                    Snackbar snackbar = Snackbar.make(view,"Registration successfully completed",Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                    snackbar.show();
                }else {
                    String erro;

                    try {
                        throw task.getException();
                        //tratando o erro do usuario digitar uma senha com menos de 6 caracteres
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Enter a password with a minimum of 6 characters!";
                        //tratando o erro do usuario digitar email invalido
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Invalid email!";
                        // tratando o erro do usuario digitar um email que ja consta no banco de dados
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "This account has already been registered!";
                    }catch (FirebaseNetworkException e){
                        erro = "No internet connection";
                    }catch (Exception e){
                        erro = "Error when registering user";
                    }
                    msgError.setText(erro);
                }
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