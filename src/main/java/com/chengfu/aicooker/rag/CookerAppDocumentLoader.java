package com.chengfu.aicooker.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 知识库文件加载
 *
 * @author: cheng fu
 **/
@Component
@Slf4j
public class CookerAppDocumentLoader {
    public final ResourcePatternResolver resourcePatternResolver;

    CookerAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadPDFs() {
        List<Document> allDocuments = new ArrayList<>();

        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.pdf");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build();
                PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);

                // 获取原始文档并确保UTF-8编码
                List<Document> documents = reader.get();
                List<Document> utf8Documents = new ArrayList<>();

                for (Document doc : documents) {
                    // 确保文本内容为UTF-8编码
                    String content = doc.getText();
                    String utf8Content = new String(content.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

                    // 创建新的Document对象，确保编码正确
                    Document utf8Document = new Document(utf8Content, doc.getMetadata());
                    utf8Documents.add(utf8Document);
                }

                allDocuments.addAll(utf8Documents);
            }
        } catch (IOException e) {
            log.error("Document loader failed", e);
        }
        return allDocuments;
    }
}
