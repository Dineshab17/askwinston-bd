package com.askwinston.web.dto;

public class DtoView {

    private DtoView() {
    }

    public static class PublicVisibility {
    }

    public static class UserVisibility extends PublicVisibility {
    }

    public static class AdminVisibility extends UserVisibility {
    }

    public static class PharmacistVisibility extends UserVisibility {
    }

    public static class DoctorVisibility extends UserVisibility {
    }

    public static class PatientVisibility extends UserVisibility {
    }

    public static class HiddenVisibility extends AdminVisibility {
    }

}
