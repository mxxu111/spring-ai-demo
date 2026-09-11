package ai.agent.web.service;


import ai.agent.web.dto.SearchResult;

import java.util.List;

public interface SearXngService {
    public List<SearchResult> search(String query);
}
