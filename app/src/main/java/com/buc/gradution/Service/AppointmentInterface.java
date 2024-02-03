package com.buc.gradution.Service;

import com.buc.gradution.Model.AppointmentModel;

public interface AppointmentInterface {
    void onDelete(AppointmentModel appointment);
    void onReschedule(AppointmentModel appointment);
}
