package io.why503.companyservice.utill;

import java.util.Random;

public class AuthCodeGenerator {

    public static String generate() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
