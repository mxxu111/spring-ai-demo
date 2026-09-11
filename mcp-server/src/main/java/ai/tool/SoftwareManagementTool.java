package ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@Slf4j
@Component
public class SoftwareManagementTool {


    /**
     * 查询本机安装的软件
     */
    @Tool(description = "查询本机已经安装的软件列表")
    public String listInstalledSoftware() {

        log.info("调用MCP工具: 查询安装软件");


        try {


            Process process = Runtime.getRuntime()
                    .exec(
                            "wmic product get name"
                    );


            String result =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream(),
                                    "GBK"
                            )
                    )
                            .lines()
                            .collect(Collectors.joining("\n"));


            return result;


        } catch (Exception e) {

            log.error(
                    "查询软件失败",
                    e
            );

            return "查询失败:" + e.getMessage();
        }

    }




    /**
     * 卸载软件
     */
    @Tool(description = "卸载本地指定软件，需要输入准确的软件名称")
    public String uninstallSoftware(@ToolParam(description="软件名称，例如 Google Chrome") String softwareName){

        log.info("调用MCP工具: 卸载软件 {}",softwareName);
        try {


            /*
             * Windows 卸载命令
             *
             * wmic product where name="xxx" call uninstall
             */


            String command = "wmic product where name=\"" + softwareName + "\" call uninstall";

            Process process = Runtime.getRuntime().exec(command);
            int code =process.waitFor();

            if(code == 0){
                return "卸载成功:" + softwareName;
            }
            return "卸载失败:"+ softwareName;



        }catch(Exception e){


            log.error(
                    "卸载软件异常",
                    e
            );


            return "卸载异常:"
                    + e.getMessage();

        }

    }


}
