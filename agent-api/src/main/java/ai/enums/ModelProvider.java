package ai.enums;

import lombok.Getter;

/**
 * 大模型提供商枚举
 * 支持多种云端大模型和本地Ollama
 */
@Getter
public enum ModelProvider {

    // 云端模型
    OPENAI("openai", "OpenAI GPT系列模型"),
    DEEPSEEK("deepseek", "DeepSeek深度求索"),
    GEMINI("gemini", "Google Gemini"),
    CLAUDE("claude", "Anthropic Claude"),
    ZHIPU("zhipu", "智谱AI GLM系列"),
    BAICHUAN("baichuan", "百川大模型"),
    QWEN("qwen", "阿里通义千问"),

    // 本地模型
    OLLAMA("ollama", "本地Ollama部署模型");

    private final String code;
    private final String description;

    ModelProvider(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ModelProvider fromCode(String code) {
        for (ModelProvider provider : values()) {
            if (provider.getCode().equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown model provider: " + code);
    }
}