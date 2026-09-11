package ai.agent.tool;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ToolRegistry} 的内存实现。
 *
 * <p>
 * 启动时自动收集容器中所有 {@link Tool} bean，并按 {@link Tool#name()} 建立索引。
 * 支持运行时动态注册与注销。
 */
@Component
public class InMemoryToolRegistry implements ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();

    public InMemoryToolRegistry(List<Tool> toolBeans) {

        if (toolBeans != null) {

            for (Tool tool : toolBeans) {
                register(tool);
            }
        }
    }

    @Override
    public Tool getTool(String name) {
        return tools.get(name);
    }

    @Override
    public Collection<Tool> getTools() {
        return Collections.unmodifiableCollection(tools.values());
    }

    @Override
    public void register(Tool tool) {
        tools.put(tool.name(), tool);
    }

    @Override
    public void unregister(String name) {
        tools.remove(name);
    }
}
