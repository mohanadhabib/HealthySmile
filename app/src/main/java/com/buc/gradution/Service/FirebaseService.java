package com.buc.gradution.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseService {
    public static final String WEB_CLIENT_ID = "101328423470-pqv65ekuels41oj66neliaa3h601unc5.apps.googleusercontent.com";
    private static FirebaseAuth firebaseAuth;
    private static FirebaseFirestore firebaseFirestore;
    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseStorage firebaseStorage;
    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
    public static FirebaseFirestore getFirebaseFirestore(){
        if(firebaseFirestore == null){
            firebaseFirestore = FirebaseFirestore.getInstance();
        }
        return firebaseFirestore;
    }
    public static FirebaseDatabase getFirebaseDatabase(){
        if(firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance().getReference().getDatabase();
        }
        return firebaseDatabase;
    }
    public static FirebaseStorage getFirebaseStorage(){
        if(firebaseStorage == null){
            firebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
        }
        return firebaseStorage;
    }
}
