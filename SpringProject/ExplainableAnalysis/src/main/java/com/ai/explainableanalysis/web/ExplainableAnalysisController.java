package com.ai.explainableanalysis.web;

import com.ai.explainableanalysis.model.Requests.VisualizationRequest;
import com.ai.explainableanalysis.model.VisualizationData;
import com.ai.explainableanalysis.service.VisualizationService;
import com.ai.explainableanalysis.service.ShapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/visualizations")
public class ExplainableAnalysisController {
    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate;
    private final ShapService shapService;
    private final VisualizationService visualizationService;

    public ExplainableAnalysisController(RestTemplate restTemplate, ShapService shapService, VisualizationService visualizationService) {
        this.restTemplate = restTemplate;
        this.shapService = shapService;
        this.visualizationService = visualizationService;
    }

    @GetMapping
    public String showVisualizationPage(Model model) {
        AddAttributes(model);

        model.addAttribute("bodyContent", "visualization");

        return "master-template";
    }

    @PostMapping
    public String getVisualization(
            @RequestParam List<String> features,
            @RequestParam String plotType,
            @RequestParam String modelType,
            Model model) throws IOException {

        AddAttributes(model);
        model.addAttribute("bodyContent", "visualization");

        String result = shapService.getVisualizationByInput(features,modelType,plotType);
        if(result !=null){
            model.addAttribute("imageData", result);
            return "master-template";
        }

        VisualizationRequest request= new VisualizationRequest(features,plotType,modelType);
        String requestBody = new ObjectMapper().writeValueAsString(request);

        ResponseEntity<String> response = sendPostRequest(requestBody);

        if (response.getStatusCode() == HttpStatus.OK) {
            String imageData = response.getBody();
            imageData=imageData.substring(1, imageData.length() - 1);

            VisualizationData visualization= visualizationService.saveImage(imageData);
            shapService.saveVisualizations(modelType,features,plotType,visualization);

            model.addAttribute("imageData", imageData);
        }

        return "master-template";
    }
    private ResponseEntity<String> sendPostRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(pythonApiUrl, HttpMethod.POST, requestEntity, String.class);
    }

    private void AddAttributes(Model model){
        model.addAttribute("previousSearches",shapService.getUserLatestVisualizations("biled"));
        model.addAttribute("features", Arrays.asList(
                "Area", "Year", "Food Inflation Rate", "Raw GDP", "GDP Growth Rate",
                "Item", "Item Price Per Tonne", "Overall Inflation Rate"
        ));
        model.addAttribute("plotTypes", Arrays.asList(
                "dependence_plot", "waterfall_plot", "summary_plot"
        ));
        model.addAttribute("modelTypes", Arrays.asList(
                "unsupervised", "supervised"
        ));
    }
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws MalformedURLException {
        Path file = Paths.get("/images/" + filename);
        Resource resource = (Resource) new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // Adjust to the correct MIME type
                .body(resource);
    }
}
