package com.lz.controller.user;

import com.lz.context.BaseContext;
import com.lz.entity.ChatMessage;
import com.lz.result.Result;
import com.lz.service.ConsultationService;
import com.lz.vo.ConsultationSessionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userConsultationController")
@RequestMapping("/user/consultation")
@Slf4j
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 1. 获取当前用户的问诊会话列表
     * (包含医生信息、状态、AI摘要等)
     */
    @GetMapping("/list")
    public Result<List<ConsultationSessionVO>> getSessionList() {
        // 从 ThreadLocal 获取当前登录用户 ID
        Long userId = BaseContext.getCurrentId();
        List<ConsultationSessionVO> list = consultationService.getSessionList(userId);
        return Result.success(list);
    }

    /**
     * 2. 获取指定会话的详细聊天记录
     * @param sessionId 会话ID
     */
    @GetMapping("/messages/{sessionId}")
    public Result<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        log.info("获取会话聊天记录: {}", sessionId);
        List<ChatMessage> list = consultationService.getMessages(sessionId);
        return Result.success(list);
    }

    /**
     * 取消问诊/会话
     * @param doctorId 医生ID
     */
    @DeleteMapping("/cancel")
    public Result cancelConsultation(@RequestParam Long doctorId) {
        log.info("用户 {} 取消与医生 {} 的问诊", doctorId);
        Long userId = BaseContext.getCurrentId();
        return consultationService.cancelConsultation(userId, doctorId);
    }

}