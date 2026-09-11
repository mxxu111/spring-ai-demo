package ai.agent.rag.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SplitterService {

    @Autowired
    private EmbeddingModel embeddingModel;

    public List<String> split(String text) {

        int chunkSize = 500;
        int overlap = 100;

        List<String> chunks = new ArrayList<>();

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(text.length(), start + chunkSize);

            String chunk = text.substring(start, end);

            chunks.add(chunk);

            start += (chunkSize - overlap);
        }

        return chunks;
    }
}
