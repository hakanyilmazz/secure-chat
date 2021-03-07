package com.hakanyilmazz.cryptomessagingapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hakanyilmazz.cryptomessagingapp.R;
import com.hakanyilmazz.cryptomessagingapp.adapter.MessageRecyclerAdapter;
import com.hakanyilmazz.cryptomessagingapp.crypto.CryptoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagingActivity extends AppCompatActivity {

    EditText messageText;
    RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    ArrayList<String> userEmailFromFirebase;
    ArrayList<String> userMessageFromFirebase;

    MessageRecyclerAdapter messageRecyclerAdapter;

    CryptoManager cryptoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        cryptoManager = CryptoManager.getInstance();

        messageText  = findViewById(R.id.messageText);

        userEmailFromFirebase       = new ArrayList<>();
        userMessageFromFirebase     = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageRecyclerAdapter = new MessageRecyclerAdapter(userEmailFromFirebase,
                userMessageFromFirebase);
        recyclerView.setAdapter(messageRecyclerAdapter);

        firebaseAuth      = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getDataFromFirestore();
    }

    public void getDataFromFirestore() {
        CollectionReference collectionReference = firebaseFirestore.collection("Messages");

        collectionReference.orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        userEmailFromFirebase.clear();
                        userMessageFromFirebase.clear();

                        if (error != null) {
                            Toast.makeText(MessagingActivity.this, error.getLocalizedMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();

                                String email     = (String) data.get("email");
                                String publicKey = (String) data.get("publicKey");
                                String message   = (String) data.get("message");
                                message          = cryptoManager.decryptToMessage(message);

                                userEmailFromFirebase.add(email);
                                userMessageFromFirebase.add(message);

                                messageRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

    }

    public void sendMessage(View view) {
        String message = messageText.getText().toString().trim();

        if (!message.equals("")) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String email = firebaseUser.getEmail();

            message = cryptoManager.encryptToMessage(message);

            HashMap<String, Object> data = new HashMap<>();

            data.put("email", email);
            data.put("date", FieldValue.serverTimestamp());
            data.put("message", message);

            firebaseFirestore.collection("Messages").add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            messageText.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MessagingActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteMessages) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Are You Sure?");
            alert.setMessage("Deleting all messages!");
            alert.setCancelable(false);

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Task<QuerySnapshot> docRef = firebaseFirestore.collection("Messages").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()) {
                                        documentSnapshot.getReference().delete();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MessagingActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MessagingActivity.this, "Messages don't deleted.", Toast.LENGTH_SHORT).show();
                }
            });

            alert.show();

        } else if (item.getItemId() == R.id.signOut) {
            firebaseAuth.signOut();

            Intent intentToSignUpActivity = new Intent(MessagingActivity.this, SignUpActivity.class);
            startActivity(intentToSignUpActivity);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}