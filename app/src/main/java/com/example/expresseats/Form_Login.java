package com.example.expresseats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Form_Login extends AppCompatActivity {

    private TextView txtCriarConta,mensagemErro;
    private EditText editEmail,editSenha;
    private Button btEntrar;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_form_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //iniciando o componente
        IniciarComponentes();

        //passando onclicklistener para o metodo
        txtCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Form_Login.this,Form_Cadastro.class);
                startActivity(intent);
            }

        });
//botão para fazer login no app
        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            //validando que o usuario preencheu os campos email e senha
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                if(email.isEmpty() || senha.isEmpty()){
                    mensagemErro.setText("Preencha todos os campos!");
                }else{
                    mensagemErro.setText("");
                    AutenticarUsuario();

                }
            }
        });
    }
    public void AutenticarUsuario(){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        //autenticando com o banco de dados o email e senha dos usuarios
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //caso o login seja um sucesso
                if (task.isSuccessful()){
                    //deixando a progressbar visivel
                    progressBar.setVisibility(View.VISIBLE);
//passando o tempo que a progressbar ficara ativa antes de mudar para tela
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IniciarTelaDeProdutos();

                        }
                    },3000);

                }else {
                    String erro;
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        erro = "Erro ao logar usuário!";
                    }
                    mensagemErro.setText(erro);
                }
            }
        });
    }

//criando a estrutura para mudar de tela
    public void IniciarTelaDeProdutos(){
        Intent intent = new Intent(Form_Login.this,Lista_Produtos.class);
        startActivity(intent);
        finish();
    }

    //metodo para verificar se o usuario está logado
    protected void onStart(){
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioAtual != null){
           IniciarTelaDeProdutos();
        }
    }

//passando o id para o componente
    public void IniciarComponentes(){
        txtCriarConta =findViewById(R.id.txtCriarConta);
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.editSenha);
        btEntrar = findViewById(R.id.btEntrar);
        mensagemErro = findViewById(R.id.mensagemErro);
        progressBar = findViewById(R.id.progressBar);
    }
}