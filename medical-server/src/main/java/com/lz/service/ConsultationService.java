package com.lz.service;

import com.lz.entity.ChatMessage;
import com.lz.vo.ConsultationSessionVO;

import java.util.List;

public interface ConsultationService {
    List<ConsultationSessionVO> getSessionList(Long userId);

    List<ChatMessage> getMessages(Long sessionId);

    void saveMessage(Long sessionId, String senderType, String content, Integer msgType);

}
