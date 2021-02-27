package net.sunnygarden.samples;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        String regexPattern;
        List<String> testStrings = new ArrayList<>();

        regexPattern = "a*[0-9]*";
        testStrings.clear();

        testStrings.add("aaa123");
        testStrings.add("aaab123");
        testStrings.add("aaa");

        for (String str : testStrings) {
            System.out.println("[" + str + "] " + (str.matches(regexPattern) ? "matches" : "doesn't match") + " with pattern[" + regexPattern + "].");
        }


        regexPattern = "^[\\+_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$"; // for email verification
        testStrings.clear();
        testStrings.add("garden96@gmail.com");
        testStrings.add("nathan.ahn@kakaocorp.com");
        testStrings.add("garden96+123@gmail.com");
        testStrings.add("nathan.ahn@");

        for (String str : testStrings) {
            System.out.println("[" + str + "] " + (str.matches(regexPattern) ? "matches" : "doesn't match") + " with pattern[" + regexPattern + "].");
        }

    }
}
