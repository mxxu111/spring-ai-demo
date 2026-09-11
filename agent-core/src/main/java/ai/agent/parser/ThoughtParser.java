package ai.agent.parser;
import ai.agent.model.Action;
import ai.agent.model.*;
/**
 * LLM 输出解析器
 * 解析 LLM 返回的思维链和动作
 */

public class ThoughtParser {



    public Thought parse(String text){
        Thought thought = new Thought();
        Action action = new Action();
        action.setName(extract(text,"Action"));
        thought.setAction(action);
        thought.setReasoning(extract(text,"Thought"));
        return thought;
    }



    private String extract(String text, String key){
        return "";
    }


}
