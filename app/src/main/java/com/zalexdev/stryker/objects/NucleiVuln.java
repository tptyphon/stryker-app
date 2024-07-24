package com.zalexdev.stryker.objects;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NucleiVuln {
    private String templateId;
    private String templateUrl;
    private String name;
    private String author;
    private String[] tags;
    private String description;
    private String[] references;
    private String severity;
    private String type;
    private String host;
    private String port;
    private String url;
    private String matchedAt;
    private String[] extractedResults;
    private String request;
    private String response;
    private String ip;
    private String timestamp;
    private boolean matcherStatus;


    public static final Comparator<NucleiVuln> SEVERITY_COMPARATOR = (vuln1, vuln2) -> Integer.compare(vuln2.getSeverityScore(), vuln1.getSeverityScore());

    @NonNull
    @Override
    public String toString() {
        return "NucleiVuln{" +
                "templateId='" + templateId + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + description + '\'' +
                ", severity='" + severity + '\'' +
                ", host='" + host + '\'' +
                ", matchedAt='" + matchedAt + '\'' +
                '}';
    }

    public static NucleiVuln fromJson(String json) {
        NucleiVuln vuln = new NucleiVuln();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            vuln.setTemplateId(getStringValue(jsonNode, "template-id"));
            vuln.setTemplateUrl(getStringValue(jsonNode, "template-url"));

            JsonNode infoNode = jsonNode.path("info");
            vuln.setName(getStringValue(infoNode, "name"));
            vuln.setAuthor(getArrayAsString(infoNode, "author"));
            vuln.setTags(getStringArray(infoNode, "tags"));
            vuln.setDescription(getStringValue(infoNode, "description"));
            vuln.setReferences(getStringArray(infoNode, "reference"));
            vuln.setSeverity(getStringValue(infoNode, "severity"));

            vuln.setType(getStringValue(jsonNode, "type"));
            vuln.setHost(getStringValue(jsonNode, "host"));
            vuln.setPort(getStringValue(jsonNode, "port"));
            vuln.setUrl(getStringValue(jsonNode, "url"));
            vuln.setMatchedAt(getStringValue(jsonNode, "matched-at"));
            vuln.setExtractedResults(getStringArray(jsonNode, "extracted-results"));
            vuln.setRequest(getStringValue(jsonNode, "request"));
            vuln.setResponse(getStringValue(jsonNode, "response"));
            vuln.setIp(getStringValue(jsonNode, "ip"));
            vuln.setTimestamp(getStringValue(jsonNode, "timestamp"));
            vuln.setMatcherStatus(getBooleanValue(jsonNode, "matcher-status"));

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return vuln;
    }

    private static String getStringValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return (fieldNode != null && !fieldNode.isNull()) ? fieldNode.asText() : "";
    }

    private static boolean getBooleanValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return (fieldNode != null && !fieldNode.isNull()) && fieldNode.asBoolean();
    }

    private static String[] getStringArray(JsonNode node, String field) {
        JsonNode arrayNode = node.get(field);
        if (arrayNode != null && arrayNode.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode element : arrayNode) {
                list.add(element.asText());
            }
            return list.toArray(new String[0]);
        }
        return new String[0];
    }

    private static String getArrayAsString(JsonNode node, String field) {
        String[] array = getStringArray(node, field);
        return String.join(", ", array);
    }

    /**
     * Get the severity as a numeric value.
     * @return An integer representing the severity:
     *         4 for critical
     *         3 for high
     *         2 for medium
     *         1 for low
     *         0 for info or unknown
     */
    public int getSeverityScore() {
        switch (severity.toLowerCase()) {
            case "critical":
                return 4;
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            case "info":
            default:
                return 0;
        }
    }

    /**
     * Get a human-readable description of the severity score.
     * @return A string describing the severity score
     */
    public String getSeverityDescription() {
        int score = getSeverityScore();
        switch (score) {
            case 4:
                return "Critical (4): Requires immediate attention";
            case 3:
                return "High (3): Should be addressed as soon as possible";
            case 2:
                return "Medium (2): Should be addressed in the near future";
            case 1:
                return "Low (1): Should be addressed when time permits";
            case 0:
            default:
                return "Info (0): Informational finding or unknown severity";
        }
    }

    public static void sortBySeverity(List<NucleiVuln> vulns) {
        vulns.sort(SEVERITY_COMPARATOR);
    }
}