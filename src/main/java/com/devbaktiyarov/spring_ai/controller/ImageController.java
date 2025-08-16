package com.devbaktiyarov.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ImageController {

    private final ChatModel chatModel;
    private final ImageModel imageModel;


    public ImageController(ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @GetMapping("/image-to-text")
    public String descrideImage() {

        return ChatClient.create(chatModel)
                .prompt()
                .user(user -> user.text("Explain what you see in image")
                        .media(MediaType.IMAGE_PNG, new ClassPathResource("images/image.png")))
                .call()
                .content();
    }

    @GetMapping("text-to-image")
    public String generateImage(@RequestParam(name = "prompt") String prompt) {

        ImageResponse response = imageModel.call(new ImagePrompt(prompt, 
                                                    OpenAiImageOptions
                                                        .builder()
                                                        .N(1)
                                                        .width(1024)
                                                        .height(1024)
                                                        .quality("hd")
                                                        .build()));

        return response.getResult().getOutput().getUrl();                                                                                       
    
    }

}
