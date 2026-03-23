package com.hms.service;

import com.hms.entity.AuditLog;
import com.hms.repository.AuditLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String userName, String action, String details) {
        AuditLog log = AuditLog.builder()
            .userName(userName == null || userName.isBlank() ? "SYSTEM" : userName)
            .action(action)
            .details(details)
            .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop20ByOrderByTimestampDesc();
    }
}
