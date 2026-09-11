package ai.tool;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailTool {

    private final JavaMailSender mailSender;
    private final String from;

    @Autowired
    private EmailTool(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    // 定义请求参数类，大模型会自动填充这些字段
    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest {
        @ToolParam(description = "收件人邮箱地址")
        private String email;

        @ToolParam(description = "发送邮件的标题/主题")
        private String subject;

        @ToolParam(description = "发送邮件的消息/正文内容")
        private String message;

        @ToolParam(description = "发送邮件的内容类型，1为HTML格式，2为普通文本格式")
        private Integer contentType;
    }

    @Tool(description = "给指定邮箱发送邮件信息。")
    public String sendEmail(EmailRequest emailRequest) {
        log.info("=================调用MCP工具：sendEmail=================");
        log.info("请求详情: {}", emailRequest);

        Integer contentType = emailRequest.getContentType();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(emailRequest.getEmail());
            mimeMessageHelper.setSubject(emailRequest.getSubject());

            // 智能处理：如果是 Markdown 格式，自动转 HTML
            if (contentType != null && contentType == 1) {
                mimeMessageHelper.setText(convertMarkdownToHtml(emailRequest.getMessage()), true);
            } else if (contentType != null && contentType == 2) {
                mimeMessageHelper.setText(emailRequest.getMessage(), true);
            } else {
                // 默认处理
                mimeMessageHelper.setText(emailRequest.getMessage());
            }

            mailSender.send(mimeMessage);
            return "邮件发送成功";

        } catch (MessagingException e) {
            log.error("发送邮件失败", e);
            return "发送邮件失败: " + e.getMessage();
        }
    }

    /**
     * 将Markdown格式的字符串转换为HTML格式
     */
    public static String convertMarkdownToHtml(String markdownStr) {
        MutableDataSet dataSet = new MutableDataSet();
        Parser parser = Parser.builder(dataSet).build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder(dataSet).build();
        return htmlRenderer.render(parser.parse(markdownStr));
    }
}

