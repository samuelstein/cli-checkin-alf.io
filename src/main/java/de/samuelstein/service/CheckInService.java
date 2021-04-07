package de.samuelstein.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class CheckInService implements QrCodeScanListener {

    public static final String APPLICATION_JSON = "application/json";
    private final String systemEndpoint;
    private final String apiKey;
    @Setter
    private String eventName;

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Override
    public void checkIn(String qrCodeContent) {
        final var splittedQrCode = StringUtils.split(qrCodeContent, "/");
        var ticketId = "";
        if (splittedQrCode.length > 0) {
            // first element in the QR Code will always be the ticket ID
            ticketId = splittedQrCode[0];
        }

        if (StringUtils.isNotBlank(ticketId) && StringUtils.isNotBlank(eventName)) {
            log.info("Try to check-in ticket #: {}", qrCodeContent);

            final var checkInUrl = String.format("%s/admin/api/check-in/event/%s/ticket/%s", systemEndpoint, eventName, ticketId);
            final var params = Map.of("code", qrCodeContent);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(checkInUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "ApiKey " + apiKey)
                    .header("Accept", APPLICATION_JSON)
                    .header("Content-Type", APPLICATION_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(new JSONObject(params).toString()))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (200 == response.statusCode()) {
                    JSONObject jsonObject = new JSONObject(response.body());
                    log.info("CHECK-IN RESPONSE: {}", jsonObject.getJSONObject("result").toString());
                    log.info("Category: {}\n", printCategory(jsonObject));
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        } else {
            log.error("No valid alf.io QR-Code or event name");
        }
    }

    private String printCategory(JSONObject jsonObject) {
        String key = "ticket";
        if (Objects.nonNull(jsonObject) && jsonObject.has(key)) {
            return jsonObject.getJSONObject(key).getString("categoryName");
        }
        return "-";
    }

    public void getAllEvents() {
        log.info("Getting all events...");
        final var eventsUrl = String.format("%s/admin/api/events", systemEndpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eventsUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "ApiKey " + apiKey)
                .header("Accept", APPLICATION_JSON)
                .header("Content-Type", APPLICATION_JSON)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Events: '{}'", response.body());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
