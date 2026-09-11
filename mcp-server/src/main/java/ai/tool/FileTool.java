package ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FileTool {

    private static final String BASE_DIR = "D:\\devolop";

    /**
     * 解析路径，确保在 BASE_DIR 下，防止路径穿越
     */
    private Path resolvePath(String relativePath) {
        Path base = Paths.get(BASE_DIR).toAbsolutePath().normalize();
        Path resolved = base.resolve(relativePath).normalize();
        if (!resolved.startsWith(base)) {
            throw new IllegalArgumentException("非法路径：不允许访问基础目录之外的文件");
        }
        return resolved;
    }

    @Tool(description = "读取指定文件的内容")
    public String readFile(@ToolParam(description = "相对于基础目录的文件路径，例如: test/hello.txt") String path) {
        log.info("=================调用MCP工具：读取文件 {}=================", path);
        try {
            Path filePath = resolvePath(path);
            if (!Files.exists(filePath)) {
                return "文件不存在: " + path;
            }
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取文件失败", e);
            return "读取文件失败: " + e.getMessage();
        }
    }

    @Tool(description = "将内容写入指定文件，如果文件不存在会自动创建，如果存在则覆盖")
    public String writeFile(
            @ToolParam(description = "相对于基础目录的文件路径") String path,
            @ToolParam(description = "要写入的文件内容") String content) {
        log.info("=================调用MCP工具：写入文件 {}=================", path);
        try {
            Path filePath = resolvePath(path);
            // 自动创建父目录
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            return "文件写入成功: " + path;
        } catch (IOException e) {
            log.error("写入文件失败", e);
            return "写入文件失败: " + e.getMessage();
        }
    }

    @Tool(description = "列出指定目录下的文件和子目录")
    public String listDirectory(@ToolParam(description = "相对于基础目录的目录路径，留空则列出基础目录") String path) {
        log.info("=================调用MCP工具：列出目录 {}=================", path);
        try {
            Path dirPath = resolvePath(path == null || path.isBlank() ? "" : path);
            if (!Files.exists(dirPath)) {
                return "目录不存在: " + path;
            }
            if (!Files.isDirectory(dirPath)) {
                return "路径不是目录: " + path;
            }
            List<String> entries = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
                for (Path entry : stream) {
                    String type = Files.isDirectory(entry) ? "[目录]" : "[文件]";
                    long size = Files.isRegularFile(entry) ? Files.size(entry) : 0;
                    entries.add(type + " " + entry.getFileName() + (size > 0 ? " (" + size + " bytes)" : ""));
                }
            }
            if (entries.isEmpty()) {
                return "目录为空";
            }
            return String.join("\n", entries);
        } catch (IOException e) {
            log.error("列出目录失败", e);
            return "列出目录失败: " + e.getMessage();
        }
    }

    @Tool(description = "删除指定文件或空目录")
    public String deleteFile(@ToolParam(description = "相对于基础目录的文件路径") String path) {
        log.info("=================调用MCP工具：删除文件 {}=================", path);
        try {
            Path filePath = resolvePath(path);
            if (!Files.exists(filePath)) {
                return "文件不存在: " + path;
            }
            Files.delete(filePath);
            return "删除成功: " + path;
        } catch (IOException e) {
            log.error("删除文件失败", e);
            return "删除文件失败: " + e.getMessage();
        }
    }
}
