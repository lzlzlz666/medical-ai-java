package com.lz.service;

import com.lz.dto.ConsultationPageQueryDTO;
import com.lz.entity.ChatMessage;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.vo.ConsultationSessionVO;

import java.util.List;

public interface ConsultationService {
    List<ConsultationSessionVO> getSessionList(Long userId);

    List<ChatMessage> getMessages(Long sessionId);

    void saveMessage(Long sessionId, String senderType, String content, Integer msgType);

    Result cancelConsultation(Long userId, Long doctorId);

    PageResult pageQuery(ConsultationPageQueryDTO queryDTO);

    Result changeStatus(Integer processId, Long userId);

    Result<List<ChatMessage>> getTempAudit(Long sessionId);

    Result audit(Long sessionId, String message);
}
