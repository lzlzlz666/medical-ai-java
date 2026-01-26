package com.lz.mapper;

import com.lz.annotation.AutoFill;
import com.lz.entity.ChatMessage;
import com.lz.entity.ConsultationSession;
import com.lz.entity.Doctor;
import com.lz.enumeration.OperationType;
import com.lz.vo.ConsultationSessionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConsultationMapper {

    // 关联查询会话列表 (XML实现)
    List<ConsultationSessionVO> getSessionListByUserId(Long userId);

    // 查询聊天记录 (注解实现简单查询)
    @Select("select * from chat_message where session_id = #{sessionId} order by create_time asc")
    List<ChatMessage> getMessagesBySessionId(Long sessionId);

    void insertMessage(ChatMessage message);

    void update(ConsultationSession consultationSession);

    @AutoFill(OperationType.INSERT)
    void insertConsultation(ConsultationSession session);
}
