package com.buc.gradution.interfaceType;

import com.buc.gradution.model.AppointmentModel;

public interface AppointmentInterface {
    void onDelete(AppointmentModel appointment);
    void onReschedule(AppointmentModel appointment);
}
