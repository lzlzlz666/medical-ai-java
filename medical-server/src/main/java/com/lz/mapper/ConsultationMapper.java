package com.lz.mapper;

import com.lz.annotation.AutoFill;
import com.lz.dto.ConsultationPageQueryDTO;
import com.lz.entity.ChatMessage;
import com.lz.entity.ConsultationSession;
import com.lz.entity.Doctor;
import com.lz.entity.TempAudit;
import com.lz.enumeration.OperationType;
import com.lz.vo.ConsultationSessionVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConsultationMapper {

    // 关联查询会话列表 (XML实现)
    List<ConsultationSessionVO> getSessionListByUserId(Long userId);

    // 查询聊天记录 (注解实现简单查询)
    @Select("select * from chat_message where session_id = #{sessionId} order by create_time asc")
    List<ChatMessage> getMessagesBySessionId(Long sessionId);

    void insertMessage(ChatMessage message);

    @AutoFill(OperationType.UPDATE)
    void update(ConsultationSession consultationSession);

    @AutoFill(OperationType.INSERT)
    void insertConsultation(ConsultationSession session);

    List<ConsultationSession> getByUserIdWithCreateTime(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    void delete(Long userId, Long doctorId);

    @Select("select * from consultation_session where user_id = #{userId} and doctor_id = #{doctorId}")
    ConsultationSession getByUserIdxWithDoctorId(Long userId, Long doctorId);

    List<ConsultationSessionVO> pageQuery(@Param("doctorId") Long doctorId,
                                   @Param("queryDTO") ConsultationPageQueryDTO queryDTO);

    List<ConsultationSession> getByUpdateTime(LocalDateTime startTime, LocalDateTime endTime);

    void insertTempAudit(Long sessionId, Long chatMessageId);

    @Select("select * from temp_audit where consultation_session_id = #{sessionId}")
    List<TempAudit> getTempAudit(Long sessionId);

    List<ChatMessage> getBatchMessages(@Param("ids") List<Long> ids);

    void updateMessage(ChatMessage message);

    @Delete("delete from temp_audit where consultation_session_id = #{sessionId}")
    void deleteTempAudit(Long sessionId);

    /**
     * 清空临时审核表所有数据
     */
    @Delete("delete from temp_audit")
    void clearTempAudit();
}
