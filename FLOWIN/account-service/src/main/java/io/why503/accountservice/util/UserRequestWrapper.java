package io.why503.accountservice.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;
/*
테스트용 wrapper 후에 gateway추가시 삭제예정
 */
public class UserRequestWrapper extends HttpServletRequestWrapper {
    //추가할 헤더들
    private final Map<String, String> headers = new HashMap<>();

    //기본 상속 생성자
    public UserRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }

    @Override
    public String getHeader(String key) {
        String value = headers.get(key);
        return value != null ? value : super.getHeader(key);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> keys = new HashSet<>(headers.keySet());
        Enumeration<String> original = super.getHeaderNames();
        while(original.hasMoreElements()){
            keys.add(original.nextElement());
        }
        return Collections.enumeration(keys);
    }

    @Override
    public Enumeration<String> getHeaders(String key) {
        if(headers.containsKey(key)){
            return Collections.enumeration(List.of(headers.get(key)));
        }
        return super.getHeaders(key);
    }
}
