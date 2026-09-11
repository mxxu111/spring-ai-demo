package ai.agent.gateway.connection;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MCPConnectionManager {


    private final Map<String,MCPConnection> connections = new ConcurrentHashMap<>();



    public void register(MCPConnection connection){

        connections.put(connection.getServerName(), connection);

    }



    public MCPConnection get(String serverName){

        return connections.get(serverName);

    }

}

