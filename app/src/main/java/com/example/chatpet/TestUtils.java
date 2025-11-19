package com.example.chatpet;

public class TestUtils {
    public static boolean isRunningTest() {
        return "true".equals(System.getProperty("IS_TEST_ENV"));
    }
}
