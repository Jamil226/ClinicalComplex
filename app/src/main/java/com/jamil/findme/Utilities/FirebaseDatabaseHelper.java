package com.jamil.findme.Utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jamil.findme.Activities.EditVisitorInfoActivity;
import com.jamil.findme.Models.Admin;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.Models.WorkShopModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseHelper {

    private static final String TAG = "TAG";
    private Context context;
    private DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference tableChats = FirebaseDatabase.getInstance().getReference("Groups");
    private DatabaseReference tblProposal = FirebaseDatabase.getInstance().getReference("Proposal");//.child("participants");
    private StorageReference folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
    private StorageReference folderProposalFiles = FirebaseStorage.getInstance().getReference().child("Proposal_files");

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void sendNotification(String title, String topic, String message, final Context context) {

        //  topic = topic.replaceAll("\\s", "");

        RequestQueue mRequestQue = Volley.newRequestQueue(context);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + topic.replaceAll("[^A-Za-z0-9]", "-"));
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", message.replaceAll(" + ", " "));
            //replace notification with data when went send data
            json.put("notification", notificationObj);
            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onError: " + error.networkResponse);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAtwGQ0ZQ:APA91bG0EJX7M1ouUVQR9mlP5kJsEQRbdUXr7OG___6ZfM-Q665AESBKQ7_9WIox2azXY5TmgsRESl3ilm1x7kugXVMBNmIfddwMGW6xqmYkod__CMYNthJp7nReAIGbWuuBotVu8rXN");
                    return header;
                }
            };


            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadUserInfo(String uid, final OnLoadUserInfoCompleteListener listener) {

        tableUser.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: Snapshot" + dataSnapshot);
                if (dataSnapshot.hasChild("workShopName"))
                    listener.onLoadUserInfoComplete(dataSnapshot.getValue(WorkShopModel.class));
                else if (dataSnapshot.hasChild("age"))
                    listener.onLoadUserInfoComplete(dataSnapshot.getValue(Admin.class));
                else
                    listener.onLoadUserInfoComplete(dataSnapshot.getValue(Visitor.class));


                tableUser.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLoadUserInfoComplete(null);
            }
        });
    }

    private void saveUserInfo(User user, final OnSaveUserCompleteListener listener) {

        tableUser.child(user.getUid()).setValue(user instanceof Visitor ?
                (Visitor) user :
                (WorkShopModel) user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    listener.onSaveUserComplete(true);
                else
                    listener.onSaveUserComplete(false);
            }
        });

    }

    public void attemptLogin(String email, String password, final OnLoginSignupAttemptCompleteListener listener) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadUserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(), new OnLoadUserInfoCompleteListener() {
                        @Override
                        public void onLoadUserInfoComplete(User user) {
                            new PreferencesManager(context).saveCurrentUser(user);
                            listener.onLoginSignupSuccess(user);
                        }
                    });
                } else
                    listener.onLoginSignupFailure(task.getException().getMessage());

            }
        });
    }
/*

    public void sendProposal(final ChatModel chatModel, final ProposalModel proposalModel, final Uri dpUri, final OnSendProposalCompleteListener listener) {

        uploadFile(dpUri, folderProposalFiles.child(dpUri.getLastPathSegment()), new OnUploadFileCompleteListener() {
            @Override
            public void onUploadFileComplete(String url) {
                proposalModel.setFile(url);
                Log.e(TAG, "onUploadFileComplete: Now in send proposal helpere");
                tblProposal.child(String.valueOf(proposalModel.getTime())).setValue(proposalModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            tableChats.child(proposalModel.getTitle()).
                                    setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    listener.onSendProposalSuccess("proposal send successfully");
                                }
                            });
                        } else {
                            listener.onSendProposalFailure(task.getException().toString());
                        }
                    }
                });
            }
        });
    }
*/

    public void attemptSignUp(final User user, String password, final Uri dpUri, final OnLoginSignupAttemptCompleteListener listener) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    uploadFile(dpUri, folderProfilePics.child(user.getUid() + ".jpg"), new OnUploadFileCompleteListener() {
                        @Override
                        public void onUploadFileComplete(String url) {
                            user.setImage(url);

                            saveUserInfo(user, new OnSaveUserCompleteListener() {
                                @Override
                                public void onSaveUserComplete(boolean isSuccessful) {
                                    new PreferencesManager(context).saveCurrentUser(user);
                                    listener.onLoginSignupSuccess(user);
                                }
                            });
                        }
                    });
                } else
                    listener.onLoginSignupFailure(task.getException().getMessage());
            }
        });
    }

    private void uploadFile(Uri fileUri, final StorageReference path, final OnUploadFileCompleteListener listener) {

        path.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "onUploadFileComplete: Now uploadiong file");

                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listener.onUploadFileComplete(uri.toString());
                        }
                    });
                }
            }
        });
    }

    public void UpdateData(final Visitor visitor, final User user,
                           Uri mainImageUri, final onVisitorDataUpdateListener listener) {
        tableUser.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Map<String, Object> postValues = new HashMap<String, Object>();
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            postValues.put(snapshot.getKey(), snapshot.getValue());
                                                        }
                                                        postValues.put("name", visitor.getName());
                                                        postValues.put("password", visitor.getPassword());
                                                        postValues.put("location", visitor.getLocation());
                                                        postValues.put("phone", visitor.getPhone());
                                                        postValues.put("image", visitor.getImage());
                                                        tableUser.child(user.getUid()).updateChildren(postValues);
                                                        listener.onVisitorDataUpdateCompleted("success");
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Log.e(TAG, "onCancelled: " + databaseError);
                                                        listener.onVisitorDataUpdateCompleted(databaseError.getMessage());
                                                    }
                                                }
                );

    }

    public void queryUsersByLocation(String location, final onQueryUserByLocationCompleteListener listener) {
        final Query query = tableUser.orderByChild("location").equalTo(location);
        tableUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Visitor> arrayList = new ArrayList<>();
                Log.e(TAG, "onDataChange: UsersList" + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Visitor visitor = dataSnapshot.getValue(Visitor.class);
                    arrayList.add(visitor);
                }
                listener.onQueryUserByLocationComplete(arrayList);
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onQueryUserByLocationComplete(null);
            }
        });

    }

    public interface onVisitorDataUpdateListener {
        void onVisitorDataUpdateCompleted(String success);
    }

    public interface onQueryUserByLocationCompleteListener {
        void onQueryUserByLocationComplete(ArrayList<Visitor> arrayList);
    }

    public void getSignleVisitorData(String uid, final onQuerySingleVisitorDataCompleteListener listener) {
        final Query query = tableUser.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Visitor> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Visitor visitor = dataSnapshot.getValue(Visitor.class);
                    arrayList.add(visitor);
                }
                listener.onQuerySingleVisitorDataComplete(arrayList);
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onQuerySingleVisitorDataComplete(null);
            }
        });
    }

    public interface onQuerySingleVisitorDataCompleteListener {
        void onQuerySingleVisitorDataComplete(ArrayList<Visitor> arrayList);
    }

    /*
        public void querySupervisorData(String campus, String dept, final OnQuerySupervisorDataCompleteListener listener) {
            String filter = campus + dept + "teacher";
            final Query query = tableUser.orderByChild("filter").equalTo(filter);
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Supervisor> arrayList = new ArrayList<>();
                    Log.e(TAG, "onDataChange: SUPERVISOR " + dataSnapshot);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Supervisor single = snapshot.getValue(Supervisor.class);
                        arrayList.add(single);
                    }

                    listener.onSupervisorDataLoaded(arrayList);
                    query.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onSupervisorDataLoaded(null);
                }
            });

        }

        public void queryStudentData(String campus, String dept, final OnQueryStudentDataCompleteListener listener) {
            String filter = campus + dept + "student";
            final Query query = tableUser.orderByChild("filter").equalTo(filter);
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Student> arrayList = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Student single = snapshot.getValue(Student.class);
                        arrayList.add(single);
                    }

                    listener.onStudentDataLoaded(arrayList);
                    query.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onStudentDataLoaded(null);
                }
            });

        }

        public void queryChats(final String cat, final String email, final OnQueryChatsDataCompleteListener listener) {

            //  final Query query = tableChats.orderByChild(cat).equalTo(uid);
            tableChats.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //  Log.e(TAG, "onDataChange: CHAT" + dataSnapshot);
                    ArrayList<ChatModel> arrayList = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final ChatModel chatModel = snapshot.getValue(ChatModel.class);
                        try {
                            if (cat.equals("participants")) {
                                // Log.e(TAG, "onDataChange:CHat " + snapshot);
                                for (int j = 0; j <= chatModel.getPartners().size(); j++) {
                                    String stt = (chatModel.getPartners().get(j).getEmail());
                                    if (stt.equals(email)) {
                                        subToTopic(chatModel.getTitle());
                                        //FirebaseMessaging.getInstance().subscribeToTopic(chatModel.getPartners().get(j).getUid().replaceAll("[^A-Za-z0-9]",chatModel.getTitle()));
                                        // Log.e(TAG, "onDataChange: ITS THE CHAT SUBSCRIPTION");
                                       */
/* TopicManagementResponse response;
                                    response = FirebaseMessaging.getInstance().subscribeToTopic(                                            chatModel.getPartners().get(0).getToken(), chatModel.getTitle().toString());
                                    *//*

                                    arrayList.add(chatModel);
                                    Toast.makeText(context, " " + chatModel.getPartners().get(j).getEmail(), Toast.LENGTH_SHORT).show();
                                    //Log.e(TAG, "onDataChange IIIIIIDDDDD: TESTING " + chatModel.getPartners().get(j).getEmail());
                                } else {
                                    Log.e(TAG, "onDataChange:  else log" + stt);
                                }
                            }
                        } else if (chatModel.getSupervisors().getEmail().equals(email)) {
                            arrayList.add(chatModel);
                            subToTopic(chatModel.getTitle());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: " + e.toString());
                    }

                }
                listener.onChattDataLoaded(arrayList);
                tableChats.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onChattDataLoaded(null);
            }
        });
    }
*/
    /*  public void subToTopic(String title) {
          title = title.replaceAll("\\s", "");
          FirebaseMessaging.getInstance().subscribeToTopic(title)
                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {

                          if (task.isSuccessful()) {
                              Log.e(TAG, "onComplete: success " + task.toString());
                          } else {
                              Log.e(TAG, "onComplete: EXP " + task.getException());
                          }
                          Log.d(TAG, task.toString());
                      }
                  });

      }
  */
   /* public void queryProposalData(final String user_id, final String cat, final OnQueryProposalDataCompleteListener listener) {
        String filter = user_id;
        final Query query;
        if (cat.equals("supervisor")) {
            query = tblProposal.orderByChild(cat).equalTo(filter);
        } else {
            query = tblProposal;
        }
        //Query query1 = tblProposal.child("uid").orderByChild("uid").equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ProposalModel> arrayList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG, "onDataChange: PROPOSAL" + dataSnapshot.getValue());
                    ProposalModel single = snapshot.getValue(ProposalModel.class);
                    if (cat.equals("supervisor")) {
                        arrayList.add(single);
                    } else {
                        try {
                            Log.e(TAG, "onDataChange: Proposal " + snapshot);
                            for (int j = 0; j <= single.getParticipants().size(); j++) {
                                String stt = (single.getParticipants().get(j).getUid());
                                if (stt.equals(user_id)) {
                                    arrayList.add(single);
                                    Log.e(TAG, "onDataChange ID For Proposal: " + single.getParticipants().get(j).getEmail());
                                } else {
                                    Log.e(TAG, "onDataChange: else log For Proposal" + stt);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: " + e.toString());
                        }
                    }

                }
                listener.onProposalDataLoaded(arrayList);
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onProposalDataLoaded(null);
            }
        });

    }
*/
   /* public void queryMessages(String cat, final OnQueryMessagesDataCompleteListener listener) {


        final Query query = tblProposal.child(cat).orderByChild("Messages");

        tableChats.child(cat).child("Messages").addValueEventListener(new ValueEventListener() {
            ArrayList<MessageModel> arrayList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: MESSAGES" + dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG, "onDataChange: messages" + dataSnapshot.getValue());
                    MessageModel single = snapshot.getValue(MessageModel.class);
                    arrayList.add(single);
                }

                listener.onMessagesDataLoaded(arrayList);
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onMessagesDataLoaded(null);
            }
        });
    }
*/
   /* public void queryProposalDataCompleted(String filter, String cat, final OnQueryProposalCompletedDataCompleteListener listener) {

        final Query query = tblProposal.orderByChild(cat).equalTo(filter);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ProposalModel> arrayList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG, "onDataChange: PROPOSAL COMPLETE" + dataSnapshot.getValue());
                    ProposalModel single = snapshot.getValue(ProposalModel.class);
                    if (single.getStatus().toString().equals("Completed")) {
                        arrayList.add(single);
                    } else {
                        Log.e(TAG, "onDataChange: I am skipping that " + single.getStatus());
                    }
                }
                listener.onProposalCompletedDataLoaded(arrayList);
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onProposalCompletedDataLoaded(null);
            }
        });
    }
*/
   /* public void queryProposalDataOngoing(String filter, String cat, final OnQueryProposalOngoingDataCompleteListener listener) {

        final Query query = tblProposal.orderByChild(cat).equalTo(filter);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ProposalModel> arrayList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e(TAG, "onDataChange: ONGOING " + dataSnapshot.getValue());
                    ProposalModel single = snapshot.getValue(ProposalModel.class);
                    if (single.getStatus().equals("Accept")) {
                        Log.e(TAG, "onDataChange: Added " + single.getStatus());
                        arrayList.add(single);
                    } else {
                        Log.e(TAG, "onDataChange: Skipping " + single.getStatus());
                    }
                }
                listener.onProposalOngoingDataLoaded(arrayList);
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onProposalOngoingDataLoaded(null);
            }
        });
    }
*/
    public void updateProposalStatus(String accepted, long time) {
        tblProposal.child(String.valueOf(time)).child("status").setValue(accepted);
    }

  /*  public void sendMessage(final MessageModel model, final String title) {
        tableChats.child(title).child("Messages").child(model.getMessageId()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               sendNotification( title+" : "+model.getSenderName(),title,model.getMessage(),context);
                Toast.makeText(context, "Message Send ", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void openFile(File url, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")) {
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "openFile EXCEPTION: " + e.toString());
            Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, "No application found which can open the file" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateToken(String token, String uid) {
        tableUser.child(uid).child("token").setValue(token);

    }


    public interface OnSaveUserCompleteListener {
        void onSaveUserComplete(boolean isSuccessful);
    }

    public interface OnLoadUserInfoCompleteListener {
        void onLoadUserInfoComplete(User user);
    }

    public interface OnLoginSignupAttemptCompleteListener {
        void onLoginSignupSuccess(User user);

        void onLoginSignupFailure(String failureMessage);
    }

    public interface OnSendProposalCompleteListener {
        void onSendProposalSuccess(String succcessMessage);

        void onSendProposalFailure(String failureMessage);
    }

    public interface OnUploadFileCompleteListener {
        void onUploadFileComplete(String url);
    }

//    public interface OnQuerySupervisorDataCompleteListener {
//        public void onSupervisorDataLoaded(ArrayList<Supervisor> supervisorsList);
//    }
//
//    public interface OnQueryStudentDataCompleteListener {
//        public void onStudentDataLoaded(ArrayList<Student> studentlist);
//    }

//    public interface OnQueryProposalDataCompleteListener {
//        public void onProposalDataLoaded(ArrayList<ProposalModel> proposalList);
//    }
//
//    public interface OnQueryMessagesDataCompleteListener {
//        public void onMessagesDataLoaded(ArrayList<MessageModel> messageModelArrayList);
//    }
//
//    public interface OnQueryProposalCompletedDataCompleteListener {
//        public void onProposalCompletedDataLoaded(ArrayList<ProposalModel> proposalList);
//    }
//
//    public interface OnQueryProposalOngoingDataCompleteListener {
//        public void onProposalOngoingDataLoaded(ArrayList<ProposalModel> proposalList);
//    }

//    public interface OnQueryChatsDataCompleteListener {
//        public void onChattDataLoaded(ArrayList<ChatModel> studentlist);
//    }

}
