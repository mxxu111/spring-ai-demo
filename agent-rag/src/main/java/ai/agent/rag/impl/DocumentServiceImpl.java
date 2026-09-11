package ai.agent.rag.impl;



import ai.agent.rag.document.DocumentService;
import ai.agent.rag.utils.CustomTextSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 整个处理流程的核心，它负责将上传的文档进行读取 -> 切分 -> 向量化 -> 存储。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private VectorStore vectorStore;

    @Override
    public void loadText(Resource resource, String fileName) {
        List<Document> documents;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (extension.matches("txt|md|json|csv|log")) {
            // 纯文本用 TextReader
            TextReader textReader = new TextReader(resource);
            textReader.getCustomMetadata().put("fileName", fileName);
            documents = textReader.get();
        } else {
            // PDF/Word/Excel/PPT 用 Tika
            documents = new TikaDocumentReader(resource).get();
            // 添加文件名元数据
            documents.forEach(doc -> doc.getMetadata().put("fileName", fileName));
        }

        // 切分
        CustomTextSplitter splitter = new CustomTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        // 存储
        vectorStore.add(chunks);
    }



    @Override
    public List<Document> doSearch(String question) {
       //向量检索相似知识库（Top3）
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(3)
                .build();

        List<Document> documents = vectorStore.similaritySearch(request);
        return documents;
    }

}
