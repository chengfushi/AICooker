package com.chengfu.aicooker;

import com.chengfu.aicooker.rag.PgVectorVectorStoreConfig;
import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class AiCookerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiCookerApplication.class, args);
	}

}
