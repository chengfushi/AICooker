package com.chengfu.aicooker.rag;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * 向量数据库配置类
 *
 * @author: cheng fu
 **/
// @Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    private CookerAppDocumentLoader cookerAppDocumentLoader;

    // @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
        // 加载文档
        List<Document> documents = cookerAppDocumentLoader.loadPDFs();
        int batchSize = 20;

        for (int i = 0; i < documents.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, documents.size());
            List<Document> batch = documents.subList(i, endIndex);
            vectorStore.add(batch);
        }
        return vectorStore;
    }
}


