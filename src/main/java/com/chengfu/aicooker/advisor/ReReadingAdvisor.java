package com.chengfu.aicooker.advisor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

/**
 * 自定义Re2Advisor
 * 可以提高大语言模型推理能力
 *
 * @author: cheng fu
 **/
@Slf4j
public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 执行请求前改写prompt
     * 
     * @author: cheng fu
     * @param: chatClientRequest
     * @return: 
     **/
    private ChatClientRequest before(ChatClientRequest chatClientRequest){
        String userText = chatClientRequest.prompt().getUserMessage().getText();

        //添加上下文参数
        chatClientRequest.context().put("re2_input_query",userText);
        String newUserText = """
                %s
                Read the question again: %s
                """.formatted(userText,userText);
        Prompt newPrompt = chatClientRequest.prompt().augmentUserMessage(newUserText);
        return new ChatClientRequest(newPrompt,chatClientRequest.context());
    }



    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return callAdvisorChain.nextCall(this.before(chatClientRequest));
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) {
        return streamAdvisorChain.nextStream(this.before(chatClientRequest));
    }

}
