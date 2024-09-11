package com.ai.explainableanalysis.web;

import com.ai.explainableanalysis.model.Requests.VisualizationRequest;
import com.ai.explainableanalysis.service.ImageService;
import com.ai.explainableanalysis.service.ShapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/visualizations")
public class ExplainableAnalysisController {
    @Value("${python.api.url}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate;
    private final ShapService shapService;
    private final ImageService imageService;

    public ExplainableAnalysisController(RestTemplate restTemplate, ShapService shapService, ImageService imageService) {
        this.restTemplate = restTemplate;
        this.shapService = shapService;
        this.imageService = imageService;
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

       /* String result = shapService.getVisualizationByInput(features,modelType,plotType);
        if(!result.equals("None")){
            model.addAttribute("imageUrl", result);
            return "visualization";
        }*/

        VisualizationRequest request= new VisualizationRequest(features,plotType,modelType);
        String requestBody = new ObjectMapper().writeValueAsString(request);

        ResponseEntity<String> response = sendPostRequest(requestBody);

        if (response.getStatusCode() == HttpStatus.OK) {
            String imageData = response.getBody();
            imageData=imageData.substring(1, imageData.length() - 1);

/*            String imageUrl=imageService.saveImage(imageData);
            shapService.saveVisualizations(modelType,features,plotType,imageUrl);*/

            model.addAttribute("imageData", imageData);
        }

        AddAttributes(model);
        model.addAttribute("bodyContent", "visualization");

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
                "Item", "Item Price Per Tonne", "Overall Inflation Rate", "label"
        ));
        model.addAttribute("plotTypes", Arrays.asList(
                "dependence_plot", "waterfall_plot", "summary_plot", "force_plot"
        ));
        model.addAttribute("modelTypes", Arrays.asList(
                "unsupervised", "supervised"
        ));
    }
}
