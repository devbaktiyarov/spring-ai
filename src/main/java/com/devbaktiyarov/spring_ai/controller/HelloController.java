package com.devbaktiyarov.spring_ai.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloController {

    private final ChatClient chatClient;

    @Value("classpath:prompt/celeb-details.st")
    private Resource celebPrompt;

    public HelloController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public String prompt(@RequestParam(name = "message") String message) {
        return chatClient.prompt(message)
                .call()
                .content();
    }

    @GetMapping("/celeb")
    public String getCelebInfo(@RequestParam(name = "name") String name) {
        String massage = """
                Give detailed information abount celebrity {name}
                """;

        PromptTemplate template = new PromptTemplate(celebPrompt);

        Prompt prompt = template.create(
                Map.of("name", name));

        return chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/sports")
    public String getSportsDetail(@RequestParam(name = "name") String name) {
        String massage = """
                Give detailed information abount sport %s
                """;
        UserMessage userMessage = new UserMessage(String.format(massage, name));

        SystemMessage systemMessage = new SystemMessage("""
                You are AI assistant. You give information about Sports.
                If someone asks something else tell that you just dont know the answer
                """);

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        return chatClient.prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();

    }

   
}
