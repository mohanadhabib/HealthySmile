package com.buc.gradution.Interface;

import com.buc.gradution.Model.AppointmentModel;

public interface AppointmentInterface {
    void onDelete(AppointmentModel appointment);
    void onReschedule(AppointmentModel appointment);
}
