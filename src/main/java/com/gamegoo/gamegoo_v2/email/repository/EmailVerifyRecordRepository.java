package com.gamegoo.gamegoo_v2.email.repository;

import com.gamegoo.gamegoo_v2.email.domain.EmailVerifyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailVerifyRecordRepository extends JpaRepository<EmailVerifyRecord, Long> {

    @Query("SELECT e FROM EmailVerifyRecord e WHERE e.email = :email ORDER BY e.updatedAt DESC")
    List<EmailVerifyRecord> findTop3ByEmailOrderByUpdatedAtDesc(@Param("email") String email);


}
