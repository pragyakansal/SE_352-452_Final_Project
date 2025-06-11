package edu.kansal_wells_xu_pina.realestate_api.utils;

public class JwtAuthResponse {
    private String jwt;

    public JwtAuthResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
