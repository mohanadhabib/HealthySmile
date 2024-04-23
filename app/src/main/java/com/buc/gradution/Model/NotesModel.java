package com.buc.gradution.Model;

public class NotesModel extends AppointmentModel{
    private String doctorNotes;
    public NotesModel(){

    }
    public NotesModel(String userId, String userName, String userEmail, String userImg, String doctorId, String doctorName, String doctorEmail, String doctorImg, String doctorSpec, String appointmentDate, String appointmentTime, String stars, String distance, String aboutDoctor, String doctorNotes) {
        super(userId, userName, userEmail, userImg, doctorId, doctorName, doctorEmail, doctorImg, doctorSpec, appointmentDate, appointmentTime, stars, distance, aboutDoctor);
        this.doctorNotes = doctorNotes;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
