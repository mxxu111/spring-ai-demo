package ai.agent.rag.document;



import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DocumentService {
    public void loadText(Resource resource, String fileName);
    public List<Document> doSearch(String question);

}
