package com.buc.gradution.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public abstract class FirebaseService {
    private static FirebaseAuth firebaseAuth;
    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseStorage firebaseStorage;
    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
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
