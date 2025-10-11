package com.chengfu.aicooker.ai;

import com.chengfu.aicooker.advisor.MyLoggerAdvisor;
import com.chengfu.aicooker.advisor.ReReadingAdvisor;
import com.chengfu.aicooker.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI厨师助手
 *
 * @author: cheng fu
 **/
@Component
@Slf4j
public class CookerApp {

    // @Resource
    // private VectorStore cookerAppVectorStore;

    // @Resource
    // private VectorStore pgVectorVectorStore;

    private final ChatClient chatClient;


    public static final String SYSTEM_PROMPT = "你叫耄大厨，是一个专业厨师智能体，需具备星级餐厅主厨级厨艺认知与服务意识，核心能力如下：\n" +
            "菜品创作：能结合用户提供的食材（需优先利用指定食材，缺漏时合理推荐替代食材）、饮食禁忌（如过敏、宗教饮食要求）、口味偏好（甜 / 咸 / 辣 / 清淡等）\n" +
            "、烹饪场景（家庭简餐 / 节日宴客 / " +
            "减脂餐等），生成步骤清晰、细节明确的食谱，包含食材用量（精确到克 / 毫升）、火候控制、调味技巧；\n" +
            "问题解答：针对烹饪中常见问题（如食材处理、火候把控、口感调整、酱汁调配等），提供科学且实用的解决方案，避免专业术语堆砌，确保新手也能理解；\n" +
            "知识延伸：适当补充菜品相关背景（如菜系起源、经典搭配逻辑），但需简洁，不喧宾夺主；\n" +
            "交互原则：回复需友好亲切，先确认用户核心需求（如未明确，主动询问食材、禁忌等关键信息），再输出针对性内容，拒绝推荐高风险烹饪方式（如未成熟食材处理）。";

    // 定义聊天报告
    record CookerReport(String title, List<String> suggestions){}

    public CookerApp(ChatModel dashscopeChatModel){

        // 初始化基于内存的对话记忆
        // MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
        //         .chatMemoryRepository(new InMemoryChatMemoryRepository())
        //         .maxMessages(20)
        //         .build();
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        // 创建基于DashScope的聊天客户端
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new MyLoggerAdvisor(),
                        new ReReadingAdvisor()
                ).build();

    }


    /**
     * AI聊天
     *
     * @author: cheng fu
     * @param: message 用户消息
     * @param chatId 会话ID
     * @return: AI聊天返回内容
     **/
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,chatId))
                .call().chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}",content);
        return content;
    }

    public CookerReport doChatWithReport(String message, String chatId) {
        CookerReport cookerReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成烹饪报告，标题为{用户名}的烹饪，内容为建议列表")
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,chatId))
                .call()
                .entity(CookerReport.class);
        log.info("cookerReport: {}", cookerReport);
        return cookerReport;
    }

    public String doChatWithRag(String message,String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,chatId))
                // .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                .call().chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}",content);
        return content;
    }

}

