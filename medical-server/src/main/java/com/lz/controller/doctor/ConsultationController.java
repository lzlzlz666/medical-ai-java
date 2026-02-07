package com.lz.controller.doctor;

import com.lz.entity.ChatMessage;
import com.lz.result.Result;
import com.lz.service.ConsultationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("doctorConsultationController")
@RequestMapping("/doctor/consultation")
@Slf4j
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * 获取暂存会话（待审核修改）的聊天记录
     * @param sessionId
     * @return
     */
    @GetMapping("/tempAudit/{sessionId}")
    public Result<List<ChatMessage>> getTempAudit(@PathVariable Long sessionId) {
        return consultationService.getTempAudit(sessionId);
    }

    /**
     * 审核通过 添加信息
     */
    @PutMapping("/audit/{sessionId}")
    public Result audit(@PathVariable Long sessionId, String message) {
        return consultationService.audit(sessionId, message);
    }

}
