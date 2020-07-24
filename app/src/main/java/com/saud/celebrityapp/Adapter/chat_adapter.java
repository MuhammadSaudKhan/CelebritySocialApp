package com.saud.celebrityapp.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Model.Message;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.emoji_checker;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class chat_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Message> list;
    Context context;

    public chat_adapter(List<Message> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                View message_layout= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message,null,false);
                return new viewHolder1(message_layout);
            case 1:
                View their_message_layout= LayoutInflater.from(parent.getContext()).inflate(R.layout.their_message,null,false);
                return new viewHolder2(their_message_layout);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                viewHolder1 v1=(viewHolder1)holder;
                if (emoji_checker.isEmojiOnly(list.get(position).getMsg())){
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.emoji);
                    v1.message_body.setTypeface(typeface);
                    v1.message_body.setText(list.get(position).getMsg());
                }else {
                    v1.message_body.setText(list.get(position).getMsg());
                }

                break;
            case 1:
                final viewHolder2 v2=(viewHolder2)holder;
                if (emoji_checker.isEmojiOnly(list.get(position).getMsg())){
                    Typeface typeface = ResourcesCompat.getFont(context, R.font.emoji);
                    v2.message_body.setTypeface(typeface);
                    v2.message_body.setText(list.get(position).getMsg());
                }else {
                    v2.message_body.setText(list.get(position).getMsg());
                }
                FirebaseFirestore.getInstance().collection("admin_profile").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            try{
                                Picasso.get().load(documentSnapshot.get("image_url").toString()).into(v2.profile);
                                v2.name.setText(documentSnapshot.get("name").toString());
                            }catch (Exception ex){
                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                break;
        }
        }
    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
         String type=list.get(position).getType();
         switch (type){
             case "sent":
                 return 0;
             case "receive":
                 return 1;
         }
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder1 extends RecyclerView.ViewHolder{
        TextView message_body;
        public viewHolder1(@NonNull View itemView) {
            super(itemView);
            message_body=itemView.findViewById(R.id.message_body);
        }
    }
    public class viewHolder2 extends RecyclerView.ViewHolder{
        TextView message_body,name;
        ImageView profile;
        public viewHolder2(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.avatar);
            message_body=itemView.findViewById(R.id.message_body);
            name=itemView.findViewById(R.id.name);
        }
    }
}
