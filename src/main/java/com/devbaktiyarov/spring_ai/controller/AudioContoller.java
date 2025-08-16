package com.devbaktiyarov.spring_ai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AudioContoller {

    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;

    public AudioContoller(OpenAiAudioTranscriptionModel audioTranscriptionModel) {
        this.audioTranscriptionModel = audioTranscriptionModel;
    }

    @GetMapping("/audio-to-text")
    public String audiTrascription() {

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("en")
                .responseFormat(TranscriptResponseFormat.TEXT)
                .temperature(0.7f)
                .build();

        AudioTranscriptionPrompt prompt = 
                new AudioTranscriptionPrompt(
                    new ClassPathResource("audio/audio.mp3"),
                options);

        return audioTranscriptionModel
                .call(prompt)
                .getResult()
                .getOutput();

    }

}
