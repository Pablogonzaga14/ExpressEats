package com.example.expresseats;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Lista_Produtos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_produtos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        int itemID = item.getItemId();
        if (itemID ==R.id.perfil){
            Intent intent = new Intent(Lista_Produtos.this,Perfil_Usuario.class);
            startActivity(intent);
        } else if (itemID ==R.id.pedidos) {
            
        } else if (itemID ==R.id.deslogar) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Lista_Produtos.this,"Usuario deslogado",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Lista_Produtos.this,Form_Login.class);
            startActivity(intent);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}