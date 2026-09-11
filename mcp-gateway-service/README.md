                         Agent Service
                              |
                              |
                    Spring AI MCP Client
                              |
                              |
                    tools/list / tools/call
                              |
                              |
                              v

                  +-----------------------+
                  |  mcp-gateway-service  |
                  |                       |
                  |   MCP Server          |
                  |                       |
                  +-----------------------+

                              |
              +---------------+---------------+
              |                               |
              v                               v

        ToolProvider                    ToolCallback


              |
              |
              v

        MCPToolRegistry
              |
              |
              v

          ToolRouter
              |
              |
              v

       MCPConnectionManager
              |
              |
              v

        MCPConnection
              |
              |
              v

        Spring AI MCP Client


              |
              |
              v


     order-mcp-server
     mysql-mcp-server
     search-mcp-server
