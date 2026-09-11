package ai.agent.rag.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

/**
 * 只需继承 TextSplitter 并重写 splitText 方法，Spring AI 的 apply 方法在内部就会调用我们的实现，非常方便。
 */
public class CustomTextSplitter extends TextSplitter {
    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }


    public String[] split(String text) {
        // 这里可以实现你自己的复杂切分逻辑
        // 为简化示例，我们按连续的换行符进行分割
        return text.split("\\s*\\R\\s*\\R\\S*");
    }
}

