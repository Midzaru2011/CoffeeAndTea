package ru.intf.sasha.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import ru.intf.sasha.dto.gigachat.AuthResponseDTO;
import ru.intf.sasha.dto.gigachat.GigaChatMessageDTO;
import ru.intf.sasha.dto.gigachat.GigaChatRequestDTO;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RequiredArgsConstructor
@Service
@Slf4j
public class GigaChatAIAgent {

//    private final GigaChatProperties properties;
    @Value("${gigachat.client.id}")
    private String clientId;

    @Value("${gigachat.client.secret}")
    private String clientSecret;
    private final RestTemplate restTemplate = new RestTemplate();
    private String removeMarkdown(String text) {
        if (text == null) return "";

        // Убираем **, *, _, ~~
        String noBoldItalics = text.replaceAll("[*_~`]", "");

        // Убираем заголовки вроде ### Текст
        String noHeaders = noBoldItalics.replaceAll("^#{1,6}\\s+", "");

        // Заменяем \n на пробел или <br> (если в HTML)

        return noHeaders.replace("\\n", "\n").trim();
    }
    // Получение токена
    private String getToken() throws Exception {
        // Генерируем уникальный идентификатор запроса
        String rqUid = UUID.randomUUID().toString();

        // Формируем учетные данные для Basic Auth
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        // Создаём заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("RqUID", rqUid);
        headers.set("Authorization", "Basic " + credentials);

        // Формируем тело запроса с scope
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "GIGACHAT_API_PERS");

        // Создаём HTTP-запрос
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        // Отправляем запрос и получаем ответ
        String authUrl = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
        ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(authUrl, request, AuthResponseDTO.class);
        log.info("Получен новый токен от GigaChat"); // <-- Логируем получение токена
        return response.getBody().getAccess_token();

    }

    // Запрос к модели
    public String getCoffeeInfo(String coffeeName) {
        try {
            String prompt = String.format("Расскажи подробно о кофе '%s'. Включи страну происхождения, вкусовые качества, рекомендации по завариванию.", coffeeName);

            GigaChatRequestDTO request = new GigaChatRequestDTO(
                    Arrays.asList(new GigaChatMessageDTO("user", prompt))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getToken());

            HttpEntity<GigaChatRequestDTO> entity = new HttpEntity<>(request, headers);

            String chatUrl = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions";

            // Отправляем запрос и получаем JSON как строку
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(chatUrl, entity, String.class);

            String rawResponse = responseEntity.getBody();
            if (rawResponse != null && !rawResponse.isBlank()) {
                log.info("Raw GigaChat Response: {}", rawResponse);
            } else {
                log.warn("Получен пустой ответ от GigaChat");
            }

            if (rawResponse == null || rawResponse.isBlank()) {
                return "Пустой ответ от GigaChat.";
            }

            // Парсим JSON вручную через ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Получаем поле "choices[0].message.content"
            JsonNode rootNode = mapper.readTree(rawResponse);
            JsonNode contentNode = rootNode
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content");

            if (contentNode == null || !contentNode.isValueNode()) {
                log.warn("Не удалось извлечь содержимое из ответа GigaChat");
                return "Не удалось извлечь информацию из ответа модели.";
            }

            String rawAnswer = contentNode.asText();
            String cleanAnswer = removeMarkdown(rawAnswer);
            log.info("Обработанный ответ для '{}': {}", coffeeName, cleanAnswer);
            return cleanAnswer;

        } catch (Exception e) {
            log.error("Ошибка при получении информации от GigaChat", e);
            return "Ошибка получения информации от GigaChat.";
        }
    }
}
