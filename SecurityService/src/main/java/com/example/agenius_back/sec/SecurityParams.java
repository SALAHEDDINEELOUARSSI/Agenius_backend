package com.example.agenius_back.sec;

public interface SecurityParams {
    public static final String JWT_HEADER_NAME="Authorization";
    public static final String SECRET="elouarssi";
    public static final long EXPIRATION=1000*24*360000;
    public static final String HEADER_PREFIX="Bearer ";
}
