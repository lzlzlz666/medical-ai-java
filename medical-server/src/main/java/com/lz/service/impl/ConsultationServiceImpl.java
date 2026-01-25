package com.lz.service.impl;

import com.lz.entity.ChatMessage;
import com.lz.entity.ConsultationSession;
import com.lz.mapper.ConsultationMapper;
import com.lz.service.ConsultationService;
import com.lz.vo.ConsultationSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationMapper consultationMapper;

    @Override
    public List<ConsultationSessionVO> getSessionList(Long userId) {
        // 调用 Mapper 进行多表关联查询
        return consultationMapper.getSessionListByUserId(userId);
    }

    @Override
    public List<ChatMessage> getMessages(Long sessionId) {
        // 根据会话ID查询消息，按时间正序排列
        return consultationMapper.getMessagesBySessionId(sessionId);
    }

    @Override
    @Transactional
    public void saveMessage(Long sessionId, String senderType, String content, Integer msgType) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderType(senderType); // "USER" 或 "AI"
        message.setContent(content);
        message.setMsgType(msgType); // 1:文本
        message.setCreateTime(LocalDateTime.now());

        ConsultationSession consultationSession = new ConsultationSession();
        consultationSession.setUpdateTime(LocalDateTime.now());
        consultationSession.setId(sessionId);
        consultationMapper.update(consultationSession);

        // 调用 Mapper 插入
        consultationMapper.insertMessage(message);
    }
}
