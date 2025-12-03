package com.sherazasghar.riverside_backend.repositories;

import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRecordingsRepository extends JpaRepository<SessionRecordings, UUID> {
    Optional<SessionRecordings> findLastBySessionIdAndIsConcluded(UUID sessionId,Boolean isConcluded);


//    @Query(
//            nativeQuery = true,
//            value = """
//                    SELECT *
//                    FROM session_recordings
//                    WHERE user_id = :hostId
//                    AND session_id = :sessionId
//                    ORDER BY created_at DESC
//                    LIMIT 1
//        """
//    )
//    Optional<SessionRecordings> getLatestRecordingFromHostAndSessionId(
//            @Param("hostId") UUID hostId,
//            @Param("sessionId") UUID sessionId
//    );

}