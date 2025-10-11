package com.chengfu.aicooker.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 向量转换
 *
 * @author: cheng fu
 **/
@Configuration
@Slf4j
public class CookerAppVectorStoreConfig {

    @Resource
    public CookerAppDocumentLoader cookerAppDocumentLoader;

    @Resource
    VectorStore pgVectorStore;

    @Bean
    VectorStore cookerAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();

        //加载文档
        List<Document> documents = cookerAppDocumentLoader.loadPDFs();
        simpleVectorStore.add(documents);

        return simpleVectorStore;
    }
}

