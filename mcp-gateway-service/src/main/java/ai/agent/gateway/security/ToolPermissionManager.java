package ai.agent.gateway.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory permission manager for gateway-managed tool calls.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolPermissionManager {

    private final Map<String, String> toolPermissions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userPermissions = new ConcurrentHashMap<>();
    private final Set<String> restrictedTools = ConcurrentHashMap.newKeySet();

    public void setToolPermission(String toolName, String permissionKey) {
        toolPermissions.put(toolName, permissionKey);
        restrictedTools.add(toolName);
        log.info("Set permission for tool: {} -> {}", toolName, permissionKey);
    }

    public void grantPermission(String caller, String permissionKey) {
        userPermissions.computeIfAbsent(caller, k -> ConcurrentHashMap.newKeySet())
                .add(permissionKey);
        log.info("Granted permission to user: {} -> {}", caller, permissionKey);
    }

    public void revokePermission(String caller, String permissionKey) {
        Set<String> permissions = userPermissions.get(caller);
        if (permissions != null) {
            permissions.remove(permissionKey);
            log.info("Revoked permission from user: {} -> {}", caller, permissionKey);
        }
    }

    public boolean requiresPermission(String toolName) {
        return restrictedTools.contains(toolName);
    }

    public boolean hasPermission(String caller, String toolName) {
        if (!requiresPermission(toolName)) {
            return true;
        }

        String requiredPermission = toolPermissions.get(toolName);
        if (requiredPermission == null) {
            return false;
        }

        Set<String> userPerms = userPermissions.get(caller);
        return userPerms != null && userPerms.contains(requiredPermission);
    }

    public String getRequiredPermission(String toolName) {
        return toolPermissions.get(toolName);
    }

    public Set<String> getUserPermissions(String caller) {
        return userPermissions.getOrDefault(caller, Collections.emptySet());
    }

    public Set<String> getRestrictedTools() {
        return Collections.unmodifiableSet(restrictedTools);
    }

    public void clearToolPermission(String toolName) {
        toolPermissions.remove(toolName);
        restrictedTools.remove(toolName);
    }

    @PostConstruct
    public void initDefaultPermissions() {
        setToolPermission("file_delete", "file:delete");
        setToolPermission("database_write", "database:write");
        setToolPermission("system_command", "system:execute");
        log.info("Initialized default tool permissions");
    }
}
