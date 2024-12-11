package com.gamegoo.gamegoo_v2.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailFacadeService {
    private final EmailService emailService;


}
