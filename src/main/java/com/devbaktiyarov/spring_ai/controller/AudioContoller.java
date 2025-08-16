package com.devbaktiyarov.spring_ai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class AudioContoller {

    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    private final OpenAiAudioSpeechModel audioSpeechModel;


    public AudioContoller(OpenAiAudioTranscriptionModel audioTranscriptionModel,
            OpenAiAudioSpeechModel audioSpeechModel) {
        this.audioTranscriptionModel = audioTranscriptionModel;
        this.audioSpeechModel = audioSpeechModel;
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

    @GetMapping("/text-to-audio/{prompt}")
    public ResponseEntity<Resource> textToAudio(@PathVariable(name = "prompt") String prompt) {

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model(OpenAiAudioApi.TtsModel.TTS_1.getValue())
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ASH)
                .speed(1.0f)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(prompt, options);
        
        SpeechResponse response = audioSpeechModel
                .call(speechPrompt);
        
        byte[] audioBytes = response.getResult().getOutput();
        
        ByteArrayResource byteArrayResource = new ByteArrayResource(audioBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        ContentDisposition.attachment()
                                .filename("audio.mp3")
                                .build().toString())
                .body(byteArrayResource);                
        
    }


}
