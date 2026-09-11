package ai.agent.web.controller;

import ai.agent.rag.document.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Autowired
    private DocumentService documentService;


    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("status", 400);
            result.put("msg", "文件不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            documentService.loadText(file.getResource(),file.getOriginalFilename());

            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();

            result.put("status", 200);
            result.put("msg", "上传成功");
            result.put("data", Map.of(
                    "filename", originalFilename,
                    "size", size
            ));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", 500);
            result.put("msg", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}