package com.buc.gradution.Service;

import android.util.Base64;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.HistoryModel;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.Model.NotesModel;
import com.buc.gradution.Model.UserModel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FirebaseSecurity {
    public String encryptProcess(String data) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte [] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedData,Base64.DEFAULT);
    }
    public String encrypt(HistoryModel history) throws Exception{
        String data = history.getImgUrl() + ",#";
        return encryptProcess(data);
    }
    public HistoryModel decryptHistory(String encryptedData) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte [] decryptedData = cipher.doFinal(Base64.decode(encryptedData,Base64.DEFAULT));
        String [] history = new String(decryptedData).split(",#");
        return new HistoryModel(history[0]);
    }
    public String encrypt(AppointmentModel appointment) throws Exception{
        String data = appointment.getUserId() + ",#" +appointment.getUserName() + ",#" + appointment.getUserEmail() + ",#" + appointment.getUserImg() + ",#" + appointment.getDoctorId() + ",#" + appointment.getDoctorName() + ",#" + appointment.getDoctorEmail() + ",#" + appointment.getDoctorImg() + ",#" + appointment.getDoctorSpec() + ",#" + appointment.getAppointmentDate() + ",#" + appointment.getAppointmentTime() + ",#" + appointment.getStars() + ",#" + appointment.getDistance() + ",#" + appointment.getAboutDoctor();
        return encryptProcess(data);
    }
    public AppointmentModel decryptAppointment(String encryptedData) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte [] decryptedData = cipher.doFinal(Base64.decode(encryptedData,Base64.DEFAULT));
        String [] appointment = new String(decryptedData).split(",#");
        return new AppointmentModel(appointment[0],appointment[1],appointment[2],appointment[3],appointment[4],appointment[5],appointment[6],appointment[7],appointment[8],appointment[9],appointment[10],appointment[11],appointment[12],appointment[13]);
    }
    public String encrypt(UserModel user) throws Exception{
        String data = user.getId() + ",#" + user.getName() + ",#" + user.getEmail() + ",#" + user.getType() + ",#" + user.getProfileImgUri() + ",#" + user.getPhoneNumber();
        return encryptProcess(data);
    }
    public UserModel decryptUser(String encryptedData) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte [] decryptedData = cipher.doFinal(Base64.decode(encryptedData,Base64.DEFAULT));
        String [] user = new String(decryptedData).split(",#");
        return new UserModel(user[0],user[1],user[2],user[3],user[4],user[5]);
    }
    public String encrypt(DoctorModel doctor) throws Exception{
        String data = doctor.getId() + ",#" + doctor.getName() + ",#" + doctor.getEmail() + ",#" + doctor.getType() + ",#" + doctor.getProfileImgUri() + ",#" + doctor.getPhoneNumber() + ",#" + doctor.getSpec() + ",#" + doctor.getStars() + ",#" + doctor.getDistance() + ",#" + doctor.getAbout();
        return encryptProcess(data);
    }
    public DoctorModel decryptDoctor(String encryptedData) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte [] decryptedData = cipher.doFinal(Base64.decode(encryptedData,Base64.DEFAULT));
        String [] doctor = new String(decryptedData).split(",#");
        return new DoctorModel(doctor[0],doctor[1],doctor[2],doctor[3],doctor[4],doctor[5],doctor[6],doctor[7],doctor[8],doctor[9]);
    }
    public String encrypt(MessageModel message) throws Exception{
        String data = message.getDoctorName() + ",#" + message.getUserName() + ",#" + message.getMessage() + ",#" + message.getReceiverId() + ",#" + message.getSenderId() + ",#" + message.getDoctorEmail() + ",#" + message.getUserEmail() + ",#" + message.getDoctorImg() + ",#" + message.getUserImg() + ",#" + message.getUserId() + ",#" + message.getDoctorId();
        return encryptProcess(data);
    }
    public MessageModel decryptMessage(String encryptedData) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte [] decryptedData = cipher.doFinal(Base64.decode(encryptedData,Base64.DEFAULT));
        String [] message = new String(decryptedData).split(",#");
        return new MessageModel(message[0],message[1],message[2],message[3],message[4],message[5],message[6],message[7],message[8],message[9],message[10]);
    }
    public String encrypt(NotesModel notes) throws Exception{
        String data = notes.getUserId() + ",#" +notes.getUserName() + ",#" + notes.getUserEmail() + ",#" + notes.getUserImg() + ",#" + notes.getDoctorId() + ",#" + notes.getDoctorName() + ",#" + notes.getDoctorEmail() + ",#" + notes.getDoctorImg() + ",#" + notes.getDoctorSpec() + ",#" + notes.getAppointmentDate() + ",#" + notes.getAppointmentTime() + ",#" + notes.getStars() + ",#" + notes.getDistance() + ",#" + notes.getAboutDoctor() + ",#" + notes.getDoctorNotes();
        return encryptProcess(data);
    }
    public NotesModel decryptNote(String encryptedData) throws Exception{
        SecretKey secretKey = new SecretKeySpec(Constant.ENCRYPTION_KEY.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte [] decryptedData = cipher.doFinal(Base64.decode(encryptedData,Base64.DEFAULT));
        String [] note = new String(decryptedData).split(",#");
        return new NotesModel(note[0],note[1],note[2],note[3],note[4],note[5],note[6],note[7],note[8],note[9],note[10],note[11],note[12],note[13],note[14]);
    }

}
