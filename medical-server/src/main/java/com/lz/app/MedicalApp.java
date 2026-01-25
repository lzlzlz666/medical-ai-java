package com.lz.app;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.lz.advisor.MyLoggerAdvisor;
import com.lz.vo.AiResultVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MedicalApp {

    @Resource
    private VectorStore medicalAppVectorStore;

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是医疗方面助手，只能回答慢性病方面";

    /**
     * 初始化 ChatClient
     *
     * @param dashscopeChatModel
     */
    public MedicalApp(ChatModel dashscopeChatModel) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param message
     * @param chatId
     * @return
     */
//    public Flux<String> doChatByStream(String message, String chatId) {
//        return chatClient
//                .prompt()
//                .user(message)
//                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
//                // 应用 RAG 知识库问答
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
//                .stream()
//                .content();
//    }

    /**
     * AI 基础对话（流式传输，包含思考过程）, 含RAG
     */
    public Flux<ChatResponse> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                // 1. 核心修改：通过 Options 开启 enableThinking
                .options(DashScopeChatOptions.builder()
                        .withEnableThinking(true) // 🔥 关键：开启思考模式
                        // .withThinkingBudget(1024) // 可选：限制思考的 Token 数
                        .build())
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new QuestionAnswerAdvisor(medicalAppVectorStore))
//                .advisors(new MyLoggerAdvisor())
                .stream()
                .chatResponse();
    }

    record LoveReport(String title, List<String> suggestions) {

    }

    /**
     * AI 恋爱报告功能（实战结构化输出）
     *
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


}
