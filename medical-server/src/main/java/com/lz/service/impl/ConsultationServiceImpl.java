package com.lz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lz.context.BaseContext;
import com.lz.dto.ConsultationPageQueryDTO;
import com.lz.entity.ChatMessage;
import com.lz.entity.ConsultationSession;
import com.lz.entity.Doctor;
import com.lz.entity.TempAudit;
import com.lz.mapper.ConsultationMapper;
import com.lz.mapper.DoctorMapper;
import com.lz.result.PageResult;
import com.lz.result.Result;
import com.lz.service.ConsultationService;
import com.lz.vo.ConsultationSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private DoctorMapper doctorMapper;

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
        message.setMsgStatus(0);
        message.setCreateTime(LocalDateTime.now());

        ConsultationSession consultationSession = new ConsultationSession();
        consultationSession.setUpdateTime(LocalDateTime.now());
        consultationSession.setId(sessionId);
        consultationMapper.update(consultationSession);

        // 调用 Mapper 插入
        consultationMapper.insertMessage(message);
        // 将数据插入到暂存表中（后续医生据此进行审核）
        Long chatMessageId = message.getId();
        consultationMapper.insertTempAudit(sessionId, chatMessageId);
    }

    @Override
    public Result cancelConsultation(Long userId, Long doctorId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN); // 00:00:00
        LocalDateTime endOfDay = now.with(LocalTime.MAX);   // 23:59:59
        List<ConsultationSession> list = consultationMapper.getByUserIdWithCreateTime(userId, startOfDay, endOfDay);
        for (ConsultationSession session : list) {
            if (session.getDoctorId().equals(doctorId)) {
                if(session.getStatus() == 0) {
                    return Result.error("您没有申请咨询");
                }
                if(session.getStatus() == 1) {
                    // consultationMapper.delete(userId, doctorId);
                    session.setStatus(0);
                    consultationMapper.update(session);
                    return Result.success("取消申请成功！");
                }
                if(session.getStatus() == 2) {
                    return Result.error("医生已同意您的申请");
                }
                if(session.getStatus() == 3) {
                    return Result.error("医生已拒绝");
                }
            }
        }
        return Result.error("请重新刷新界面");
    }

    public PageResult pageQuery(ConsultationPageQueryDTO queryDTO) {
        // 1. 设置分页参数
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Long doctorId = BaseContext.getCurrentId();

        // 2. 执行查询
        // 这里的 list 会被 PageHelper 自动拦截并包装成 Page 对象
        List<ConsultationSessionVO> list = consultationMapper.pageQuery(doctorId, queryDTO);

        Page<ConsultationSessionVO> p = (Page<ConsultationSessionVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 处理患者咨询
     * @param processId  // 2是接收，3是拒绝
     * @return
     */
    public Result changeStatus(Integer processId, Long userId) {
        Long doctorId = BaseContext.getCurrentId();
        Doctor doctor = doctorMapper.getById(doctorId);
        if(processId == 2) {
            if(doctor.getMaxDailyAudit() <= 0) {
                return Result.error("您今日接收的咨询已满");
            }
            doctor.setMaxDailyAudit(doctor.getMaxDailyAudit() - 1);
            doctorMapper.update(doctor);
            ConsultationSession session = consultationMapper.getByUserIdxWithDoctorId(userId, doctorId);
            if(session.getStatus() == 1) {
                session.setStatus(2);
                consultationMapper.update(session);
                return Result.success("接收成功");
            }
        } else if(processId == 3) {
            ConsultationSession session = consultationMapper.getByUserIdxWithDoctorId(userId, doctorId);
            if(session.getStatus() == 1) {
                session.setStatus(3);
                consultationMapper.update(session);
                return Result.success("拒绝成功");
            }
        }
        return Result.error("请重新刷新页面");
    }

    /**
     * 获取会话的临时审核结果
     * @param sessionId
     * @return
     */
    public Result<List<ChatMessage>> getTempAudit(Long sessionId) {
        List<TempAudit> list = consultationMapper.getTempAudit(sessionId);
        // 2. 判空处理（非常重要！）
        // 如果没有查到任何审核记录，直接返回空列表，否则后面 stream 操作或数据库查询会报错
        if (list == null || list.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 3. 使用 Stream 流提取所有的 chat_message_id
        List<Long> messageIds = list.stream()
                .map(TempAudit::getChatMessageId) // 这里假设 TempAudit 类里有 getChatMessageId 方法
                .distinct()                       // 可选：去重，防止重复 ID 查多次
                .collect(Collectors.toList());

        List<ChatMessage> messages = consultationMapper.getBatchMessages(messageIds);

        return Result.success(messages);
    }

    /**
     * 审核通过 添加信息
     */
    @Transactional
    public Result audit(Long sessionId, String message) {
        // 删除暂存的审核信息
        consultationMapper.deleteTempAudit(sessionId);
        // 修改最后一条消息的状态，并插入doctor_summary
        List<ChatMessage> messageList = consultationMapper.getMessagesBySessionId(sessionId);
        ChatMessage lastMessage = messageList.getLast();
        lastMessage.setMsgStatus(1);
        lastMessage.setDoctorSummary(message);
        consultationMapper.updateMessage(lastMessage);

        return Result.success("审核通过，添加信息成功");
    }

}
