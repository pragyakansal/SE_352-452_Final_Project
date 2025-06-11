package edu.kansal_wells_xu_pina.realestate_api.enums;

public enum Role {
    BUYER,
    AGENT,
    ADMIN;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}

