package com.jamil.findme.Utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.jamil.findme.Models.Admin;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.FeedBackModel;
import com.jamil.findme.Models.GeneralRepairModel;
import com.jamil.findme.Models.MessageModel;
import com.jamil.findme.Models.PostModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.Models.WorkShopModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseHelper {

    private static final String TAG = "TAG";
    private Context context;
    private DatabaseReference generalVehicleMaintenance = FirebaseDatabase.getInstance().getReference().child("VehicleMaintenance");
    private DatabaseReference generalRepair = FirebaseDatabase.getInstance().getReference().child("GeneralRepair");
    private DatabaseReference tableFeedBack = FirebaseDatabase.getInstance().getReference().child("FeedBacks");
    private DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference tableChats = FirebaseDatabase.getInstance().getReference("Chats");
    private DatabaseReference tblProposal = FirebaseDatabase.getInstance().getReference("Proposal");//.child("participants");
    private StorageReference folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
    private DatabaseReference tablePosts = FirebaseDatabase.getInstance().getReference().child("SpareParts");
    private StorageReference folderPosts = FirebaseStorage.getInstance().getReference().child("spare_part_images/");
    private StorageReference folderGeneralProducts = FirebaseStorage.getInstance().getReference().child("general_products/");
    private StorageReference folderVehicleMaintenance = FirebaseStorage.getInstance().getReference().child("general_vehicle_maintenance/");

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void loadUserInfo(String uid, final OnLoadUserInfoCompleteListener listener) {

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

    public void uploadFile(Uri fileUri, final StorageReference path, final OnUploadFileCompleteListener listener) {

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

    public void UpdateData(final Visitor visitor, final User user, Uri mainImageUri, final onVisitorDataUpdateListener listener) {
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

    public void queryUsersByLocation(String type, String location, final onQueryUserByLocationCompleteListener listener) {
        final Query query;
        if (type.equals("Admin")) {
            query = tableUser;
        } else {
            query = tableUser.orderByChild("location").equalTo(location);

        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Visitor> arrayList = new ArrayList<>();
                arrayList.clear();
                Log.e(TAG, "onDataChange: UsersList" + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Visitor visitor = dataSnapshot.getValue(Visitor.class);
                    assert visitor != null;
                    if (visitor.getType().equals("User"))
                        arrayList.add(visitor);
                }
                listener.onQueryUserByLocationComplete(arrayList);
                arrayList.clear();
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onQueryUserByLocationComplete(null);
            }
        });

    }

    public void deleteUserById(String uid) {
        tableUser.child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "User Data Delweted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void queryWorkShopData(String uid, String type, final onQueryWorkShopDataCompleteListener listener) {
        final Query query = tableUser.orderByChild("type").equalTo("WorkShop");
        tableUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<WorkShopModel> arrayList = new ArrayList<>();
                Log.e(TAG, "onDataChange: UsersList" + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WorkShopModel visitor = dataSnapshot.getValue(WorkShopModel.class);
                    assert visitor != null;
                    if (visitor.getType().equals("WorkShop"))
                        arrayList.add(visitor);
                }
                listener.onQueryWorkShopDataCompleteListener(arrayList);
                arrayList.clear();
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onQueryWorkShopDataCompleteListener(null);
            }
        });

    }

    public void querySparePartsData(final onQuerySparePartsDataCompleteListener listener) {
        tablePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PostModel> arrayList = new ArrayList<>();
                arrayList.clear();
                Log.e(TAG, "onDataChange: UsersList" + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel visitor = dataSnapshot.getValue(PostModel.class);
                    arrayList.add(visitor);
                }
                listener.onSparePartsDataCompleted(arrayList);
                arrayList.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSparePartsDataCompleted(null);
            }
        });
    }

    public void sendFeedBack(FeedBackModel feedBackModel, final onSendFeedBackDataCompleteListener listener) {
        tableFeedBack.child(feedBackModel.getFid()).setValue(feedBackModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onSendFeedBackDataCompleted("success");
                } else {
                    listener.onSendFeedBackDataCompleted(task.getException().toString());
                }

            }
        });
    }

    public void getFeedBacks(final onRetrieveFeedBackDataCompleteListener listener) {
        tableFeedBack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<FeedBackModel> arrayList = new ArrayList<>();
                arrayList.clear();
                Log.e(TAG, "onDataChange: UsersList" + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FeedBackModel feedBackModel = dataSnapshot.getValue(FeedBackModel.class);
                    arrayList.add(feedBackModel);
                }
                listener.onRetrieveFeedBackDataCompleted(arrayList);
                arrayList.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onRetrieveFeedBackDataCompleted(null);
            }
        });
    }

    public void queryGeneralArticles(final onGeneralRepairArticlesDataCompleteListener listener) {
        generalRepair.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<GeneralRepairModel> arrayList = new ArrayList<>();
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GeneralRepairModel visitor = dataSnapshot.getValue(GeneralRepairModel.class);
                    arrayList.add(visitor);
                }
                listener.onRetrieveFeedBackDataCompleted(arrayList);
                arrayList.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onRetrieveFeedBackDataCompleted(null);
            }
        });

    }
    public void queryGeneralVehicleMaintenanceData(final onGeneralVehicleMaintenanceDataCompleteListener listener) {
        generalVehicleMaintenance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<GeneralRepairModel> arrayList = new ArrayList<>();
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GeneralRepairModel visitor = dataSnapshot.getValue(GeneralRepairModel.class);
                    arrayList.add(visitor);
                }
                listener.onGeneralVehicleMaintenanceDataCompleted(arrayList);
                arrayList.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGeneralVehicleMaintenanceDataCompleted(null);
            }
        });

    }

    public void addGeneralRepairItem(final GeneralRepairModel postModel, Uri newPostImgUri, final onAddGeneralRepairItemCompleteListener listener) {
        uploadFile(newPostImgUri, folderGeneralProducts, new OnUploadFileCompleteListener() {
            @Override
            public void onUploadFileComplete(String url) {
                postModel.setImage(url);
                generalRepair.child(postModel.getId()).setValue(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onAddGeneralRepairItemCompleted("success");
                        } else {
                            listener.onAddGeneralRepairItemCompleted(task.getException().toString());
                        }
                    }
                });
            }
        });

    }

    public void addGeneralVehicleMaintenanceItem(final GeneralRepairModel postModel, Uri newPostImgUri, final onAddGeneralVehicleMaintenanceItemCompleteListener listener) {
        uploadFile(newPostImgUri, folderVehicleMaintenance, new OnUploadFileCompleteListener() {
            @Override
            public void onUploadFileComplete(String url) {
                postModel.setImage(url);
                generalVehicleMaintenance.child(postModel.getId()).setValue(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onVehicleMaintenanceItemCompleted("success");
                        } else {
                            listener.onVehicleMaintenanceItemCompleted(task.getException().toString());
                        }
                    }
                });
            }
        });

    }

    public interface onAddGeneralVehicleMaintenanceItemCompleteListener {
        void onVehicleMaintenanceItemCompleted(String models);
    }

    public interface onAddGeneralRepairItemCompleteListener {
        void onAddGeneralRepairItemCompleted(String models);
    }

    public interface onGeneralVehicleMaintenanceDataCompleteListener {
        void onGeneralVehicleMaintenanceDataCompleted(ArrayList<GeneralRepairModel> models);
    }  public interface onGeneralRepairArticlesDataCompleteListener {
        void onRetrieveFeedBackDataCompleted(ArrayList<GeneralRepairModel> models);
    }

    public interface onRetrieveFeedBackDataCompleteListener {
        void onRetrieveFeedBackDataCompleted(ArrayList<FeedBackModel> models);
    }

    public interface onSendFeedBackDataCompleteListener {
        void onSendFeedBackDataCompleted(String models);
    }

    public interface onQuerySparePartsDataCompleteListener {
        void onSparePartsDataCompleted(ArrayList<PostModel> models);
    }

    public interface onQueryWorkShopDataCompleteListener {
        void onQueryWorkShopDataCompleteListener(ArrayList<WorkShopModel> models);
    }

    //Send the spare part data
    public void sendPost(final PostModel postModel, final User currentUser, Uri newPostImgUri,
                         final OnPostCompleteListener listener) {
        uploadFile(newPostImgUri, folderPosts.child(System.currentTimeMillis()
                + currentUser.getUid()), new OnUploadFileCompleteListener() {
            @Override
            public void onUploadFileComplete(String url) {
                postModel.setImage(url);
                postModel.setPost_id(System.currentTimeMillis()
                        + currentUser.getUid());
                tablePosts.child(postModel.getTime() + postModel.getUser_id()).setValue(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onPostCompleted("post send successfully");
                        } else {
                            listener.onPostCompleted("error in sending post :" + task.getException());
                        }
                    }
                });
            }
        });


    }

    public interface OnPostCompleteListener {
        void onPostCompleted(String isSuccessful);
    }

    public interface onVisitorDataUpdateListener {
        void onVisitorDataUpdateCompleted(String success);
    }

    public interface onQueryUserByLocationCompleteListener {
        void onQueryUserByLocationComplete(ArrayList<Visitor> arrayList);
    }

    public interface onQuerySingleVisitorDataCompleteListener {
        void onQuerySingleVisitorDataComplete(ArrayList<Visitor> arrayList);
    }

    public void queryMessages(String cat, final OnQueryMessagesDataCompleteListener listener) {


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

    public void sendMessage(final MessageModel model, final String title) {
        tableChats.child(title).child("Messages").child(model.getMessageId()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Message Send ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnQueryMessagesDataCompleteListener {
        void onMessagesDataLoaded(ArrayList<MessageModel> messageModelArrayList);
    }


    public void queryChats(final User currentUser, final OnQueryChatsDataCompleteListener listener) {

        tableUser.child(currentUser.getUid()).child("ChatList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: Chats" + dataSnapshot);
                ArrayList<ChatModel> arrayList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    Log.e(TAG, "onDataChange: Name" + chatModel.getName());
                    arrayList.add(chatModel);
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

    public interface OnQueryChatsDataCompleteListener {
        void onChattDataLoaded(ArrayList<ChatModel> studentlist);
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

    public interface OnUploadFileCompleteListener {
        void onUploadFileComplete(String url);
    }

}
