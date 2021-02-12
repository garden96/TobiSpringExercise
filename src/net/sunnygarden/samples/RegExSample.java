package net.sunnygarden.samples;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegExSample {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        Pattern pattern = Pattern.compile("<[^<>]*>");
        Matcher matcher = pattern.matcher("배를 먹습니다. <br />사과를 먹습니다.");

        boolean found = false;

        while (matcher.find()) {
            sb.append("텍스트 \"")
                    .append(matcher.group())      // 찾은 문자열 그룹 입니다.
                    .append("\"를 찾았습니다.\n")
                    .append("인덱스 ")
                    .append(matcher.start())      // 찾은 문자열의 시작 위치 입니다.
                    .append("에서 시작하고, ")
                    .append(matcher.end())        // 찾은 문자열의 끝 위치 입니다.
                    .append("에서 끝납니다.\n");
            found = true;
        }
        if (!found) {
            sb.append("찾지 못했습니다.");
        }
        System.out.println(sb.toString());
    }
}
