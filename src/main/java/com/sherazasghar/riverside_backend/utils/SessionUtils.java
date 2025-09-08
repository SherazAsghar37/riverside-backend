package com.sherazasghar.riverside_backend.utils;

import java.util.UUID;

public interface SessionUtils {
     static String generateSessionCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
