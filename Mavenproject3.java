package com.fasterxml.jackson.databind.mavenproject3;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Mavenproject3 {

    private static final String OPENAI_API_KEY = "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";  // Replace with your OpenAI API key
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";

    public static void main(String[] args) {
        // Create the GUI
        JFrame frame = new JFrame("Test Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Text field for entering the topic
        JTextField topicField = new JTextField(20);
        JLabel topicLabel = new JLabel("Enter a topic:");

        // Button to generate questions
        JButton generateButton = new JButton("Generate Questions");
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setOpaque(false);
        resultArea.setEditable(false);
        resultArea.setFocusable(false);

        generateButton.addActionListener(e -> {
            String topic = topicField.getText();
            String questions = generateQuestions(topic);
            resultArea.setText(questions);
        });

        // Add components to the frame
        frame.setLayout(new FlowLayout());
        frame.add(topicLabel);
        frame.add(topicField);
        frame.add(generateButton);
        frame.add(new JScrollPane(resultArea));

        // Display the frame
        frame.setVisible(true);
    }

    private static String generateQuestions(String topic) {
        OkHttpClient client = new OkHttpClient();

        // Prepare the request JSON
        String prompt = "Generate 5 questions based on the following topic: " + topic;
        String requestBodyJson = String.format(
            "{\"model\": \"text-davinci-003\", \"prompt\": \"%s\", \"max_tokens\": 150, \"n\": 1}", prompt);

        RequestBody body = RequestBody.create(requestBodyJson, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(body)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response); {
            System.out.println(response.body().string());
        }
               
            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body().string());
            JsonNode choices = rootNode.path("choices");

            StringBuilder questions = new StringBuilder();
            for (JsonNode choice : choices) {
                questions.append(choice.path("text").asText().trim()).append("\n");
            }
            return questions.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating questions.";
        }
    }
}