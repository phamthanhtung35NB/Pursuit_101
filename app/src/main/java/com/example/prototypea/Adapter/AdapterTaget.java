package com.example.prototypea.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototypea.Class.ItemList;
import com.example.prototypea.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterTaget extends RecyclerView.Adapter<AdapterTaget.MyViewHolder> {
    private List<ItemList> itemLists;
    private final Context context;

    public AdapterTaget(List<ItemList> messagesList, Context context) {
        this.itemLists = messagesList;
        this.context = context;
        System.out.println("MessagesAdapter.MessagesAdapter");
    }

    @NonNull
    @Override
    public AdapterTaget.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tasks, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTaget.MyViewHolder holder, int position) {
    ItemList item = itemLists.get(position);
    holder.textContent.setText(item.getContent());
    holder.imageButton.setImageResource(R.drawable.logov);
    holder.radioDone.setChecked(false);
    holder.radioUnDone.setChecked(false);
    if (item.getStatus().equals("Đã Thực Hiện Được")) {
        holder.radioDone.setChecked(true);
    } else if (item.getStatus().equals("Chưa Thực Hiện Được")) {
        holder.radioUnDone.setChecked(true);
    }
        Locale vietnam = new Locale("vi", "VN");
        SimpleDateFormat day = new SimpleDateFormat("ddMMyyyy", vietnam);
        String day1 = day.format(new Date());
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String key = item.getKey();

    holder.radioDone.setOnClickListener(v -> {
        if (item.getStatus().equals("Chưa Thực Hiện Được")) {
            dbRef.child(uid).child("mission").child(day1).child(key).child("status").setValue("Đã Thực Hiện Được");
            //lấy giá trị dbRef.child(uid).child("mission").child(day1).child("done")
            dbRef.child(uid).child("mission").child(day1).child("done").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer done = dataSnapshot.getValue(Integer.class);
                    if (done == null) {
                        done = 0;
                    }
                    done++;
                    dbRef.child(uid).child("mission").child(day1).child("done").setValue(done);
                    updateData(uid, day1, done);
                    // Now you can use the "done" value
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
            //ứuả trên firestore

        }
    });
    holder.radioUnDone.setOnClickListener(v -> {
        if(item.getStatus().equals("Đã Thực Hiện Được")){
            dbRef.child(uid).child("mission").child(day1).child(key).child("status").setValue("Chưa Thực Hiện Được");

            dbRef.child(uid).child("mission").child(day1).child("done").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer done = dataSnapshot.getValue(Integer.class);
                    if (done == null) {
                        done = 0;
                    }
                    done--;
                    dbRef.child(uid).child("mission").child(day1).child("done").setValue(done);
                    updateData(uid, day1, done);
                    // Now you can use the "done" value
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    });

}
    public static void updateData(String accountId, String day, long newDone) {
        FirebaseFirestore db3 = FirebaseFirestore.getInstance();
        DocumentReference docRef = db3.collection(accountId).document(day);

        Map<String, Object> updates = new HashMap<>();
        updates.put("done", newDone);

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
public static void updateData(String accountId, String day, long newDone, long newSum) {
    FirebaseFirestore db3 = FirebaseFirestore.getInstance();
    DocumentReference docRef = db3.collection(accountId).document(day);

    Map<String, Object> updates = new HashMap<>();
    updates.put("done", newDone);
    updates.put("sum", newSum);

    docRef.update(updates)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
}
    public void updateList(List<ItemList> list){
        this.itemLists=list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return itemLists.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textContent;
        private RadioButton radioDone, radioUnDone;
        private CircleImageView imageButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.textContent);
            radioDone = itemView.findViewById(R.id.radioDone);
            radioUnDone = itemView.findViewById(R.id.radioUnDone);
            imageButton = itemView.findViewById(R.id.imageButton);
        }
    }
}