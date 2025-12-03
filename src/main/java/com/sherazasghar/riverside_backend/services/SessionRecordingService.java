package com.sherazasghar.riverside_backend.services;


import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;

import java.util.UUID;

public interface SessionRecordingService {
    SessionRecordings startRecording(UUID userId,String sessionCode);
    void stopRecording(UUID userId,String sessionCode);
}
