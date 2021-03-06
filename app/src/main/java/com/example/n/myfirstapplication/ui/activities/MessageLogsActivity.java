package com.example.n.myfirstapplication.ui.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.n.myfirstapplication.dto.Contact;
import com.example.n.myfirstapplication.dto.MessageLog;
import com.example.n.myfirstapplication.R;
import com.example.n.myfirstapplication.dto.User;
import com.example.n.myfirstapplication.ui.adapters.MessageLogListAdapter;
import com.example.n.myfirstapplication.untilities.FirebaseReferences;
import com.example.n.myfirstapplication.untilities.FirebaseStrings;
import com.example.n.myfirstapplication.untilities.UserGlobalValues;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This gets the users' contacts with newest notfications and display them in the notification tab
 * Created by john
 */
public class MessageLogsActivity extends Fragment {
    private static final String TAG ="Notifications";
    private ListView messageLogsLv;
    private MessageLogListAdapter adapter;
    private DatabaseReference mContactsRef;
    private TextView numConversations;
    private HashMap<String,MessageLog> messageLogList;
    private Intent messagesScreen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate new screen using message log layout
        View view = inflater.inflate(R.layout.activity_message_logs,container,false);

        // Set the title
        getActivity().setTitle(TAG);
        messagesScreen = new Intent(getActivity(), MessageScreenActivity.class);

        // This screen will not be saved in the back history
        messagesScreen.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        messageLogsLv = (ListView) view.findViewById(R.id.messagelogs_listview);
        messageLogList= new HashMap<>();
        numConversations = (TextView) view.findViewById(R.id.noConvo_tv);

        // Getting Firebase references
        mContactsRef= FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(FirebaseStrings.CONTACTS);

        // Getting the contact list
        mContactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                messageLogList.clear();
                // Each datasnapshot is a contact instance
                // We populate the contact list and pass it to the adapter which does the UI stuff
                for(DataSnapshot child: children) {
                    String messageLogId = child.getKey();
                    Contact contact = child.getValue(Contact.class);
                    if(!(contact.lastMessage.equals("") && contact.getDate().equals(""))){
                        int numNoti;
                        if(contact.getNumNotifications()==null ||contact.getNumNotifications().equals("")){
                            numNoti =0;
                        }else{
                            numNoti = Integer.parseInt(contact.getNumNotifications());
                        }
                        messageLogList.put(contact.getEmail(),
                                new MessageLog(messageLogId, contact.getName(),contact.getLastMessage(),
                                        contact.getDate(),numNoti));
                    }
                }
                // Find the profile pictures for each contact and set adapter
                setAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }

    /**
     * Set the adapter to display the ordered notification/message logs with profile picture
     * in the notifications tab
     */
    private void setAdapter (){

        // Get contact profile Uri
        FirebaseReferences.USER_NODE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Store the uri for each contact on an external hashmap, bad code. but a Work-around
                // for an ambiguous problem in MessageAdapter
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if(messageLogList.get(user.getEmail().trim())!= null){
                        messageLogList.get(user.getEmail().trim()).setProfileUri(user.getProfilePicUri());
                        UserGlobalValues.contactProfileURIs.put(user.getEmail().trim().toLowerCase(),user.getProfilePicUri());
                    } else if(user.getEmail().trim().toLowerCase()
                            .equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim().toLowerCase())){
                        UserGlobalValues.contactProfileURIs
                                .put(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim().toLowerCase()
                                        ,user.getProfilePicUri());
                    }
                }

                // Rearrange the list by number of new notifications, latest timestamp and name
                List<MessageLog> list = new ArrayList<MessageLog>(messageLogList.values());
                Collections.sort(list);

                // Pass the list of message_receiver.xml log to the adapter
                adapter = new MessageLogListAdapter(getActivity().getApplicationContext(), list);
                messageLogsLv.setAdapter(adapter);

                // Set text view
                if(messageLogList.keySet().size() >1){
                    numConversations.setText(messageLogList.keySet().size() +" conversations");
                }else{
                    numConversations.setText(messageLogList.keySet().size() +" conversation");
                }

                // Add listeners to each contact item, open the notification history when clicked
                messageLogsLv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id",view.getTag().toString());
                        bundle.putString("title",((MessageLog)adapter.getItem(i)).getUserName());
                        messagesScreen.putExtras(bundle);
                        getActivity().startActivity(messagesScreen);
                        // Update number of unseen notifications
                        MessageLog ml= (MessageLog) adapterView.getAdapter().getItem(i);
                        // When the user click on the message log aka notification history
                        // Set the notification to 0
                        FirebaseReferences.USER_NODE
                                .child(FirebaseReferences.MY_AUTH.getCurrentUser().getUid())
                                .child(FirebaseStrings.CONTACTS).child(ml.getMessageLogId())
                                .child(FirebaseStrings.NUMNOTIFICATIONS).setValue("0");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
