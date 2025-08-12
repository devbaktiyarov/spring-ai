package com.devbaktiyarov.spring_ai.controller;

import com.devbaktiyarov.spring_ai.model.Achivement;
import com.devbaktiyarov.spring_ai.model.Player;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    private final ChatClient chatClient;

    public PlayerController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/sposts")
    public List<Player> getSportsPlayersAchivement(@RequestParam(name = "name") String name) {

        BeanOutputConverter<List<Player>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Player>>() {
                });

        String message = """
                Get a list of carrer achivement for persons in sport {name}
                use player name as key and achivements as the value
                {format}""";

        PromptTemplate promptTemplate = new PromptTemplate(message);

        Prompt prompt = promptTemplate.create(Map.of("name", name, "format", converter.getFormat()));

        var response = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        if (response == null) {
            throw new IllegalArgumentException("Chat reponse is null");
        }

        String result = response
                .getResult()
                .getOutput()
                .getText();
         
        if(result == null) {
            throw new IllegalArgumentException("Output result is null");
        }        

        return converter.convert(result);
    }


    @GetMapping("/achivements")
    public List<Achivement> getAchivement(@RequestParam(name = "name") String name) {
        
        String message = """
                Get list of achivements for {name}
                """;
        
        PromptTemplate promptTemplate = new PromptTemplate(message);
        
        Prompt prompt = promptTemplate.create(Map.of("name", name));


        return chatClient.prompt(prompt)
                    .call()
                    .entity(new ParameterizedTypeReference<List<Achivement>>() {});
                
    }
    
}