package com.lz.service;

import com.lz.dto.ChatConfigDTO;
import com.lz.service.impl.MedicalAppServiceImpl;
import com.lz.vo.MedicalReportVO;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MedicalAppService {

    Flux<ChatResponse> doChatByStream(ChatConfigDTO cfg);

    MedicalReportVO doChatWithReport(String message, String chatId);

//    String doChatWithImagesSync(ChatConfigDTO cfg);

    Flux<ChatResponse> doChatWithImagesByStream(ChatConfigDTO cfg);
}
