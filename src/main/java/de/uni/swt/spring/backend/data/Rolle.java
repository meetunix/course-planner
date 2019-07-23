package de.uni.swt.spring.backend.data;

public class Rolle {
    public static final String ADMIN = "admin";
    public static final String LEHRGANGSLEITER = "lehrgangsleiter";
    public static final String DOZENT = "dozent";
    public static final String STUDENT = "student";

    private Rolle() {
    }

    public static String[] getAllRoles() {
        return new String[]{ADMIN, LEHRGANGSLEITER, DOZENT, STUDENT};
    }
}
