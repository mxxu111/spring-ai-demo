package ai.agent.gateway.exception;

/**
 * MCP Gateway异常类
 */
public class MCPGatewayException extends RuntimeException {

    public MCPGatewayException(String message) {
        super(message);
    }

    public MCPGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

}