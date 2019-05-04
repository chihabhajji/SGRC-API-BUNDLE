package bte.sgrc.SpringBackend.api.enums;

public enum ProfileEnum{
    ROLE_ADMIN,
    ROLE_CUSTOMER,
    ROLE_TECHNICIAN;

    public static ProfileEnum getProfile(String profile) {
        switch (profile) {
            case "ROLE_CUSTOMER":return ROLE_CUSTOMER;
            case "ROLE_ADMIN":return ROLE_ADMIN;
            case "ROLE_TECHNICIAN":return ROLE_TECHNICIAN;
            default:return ROLE_CUSTOMER;
        }
    }
}