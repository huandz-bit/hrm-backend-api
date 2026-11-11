package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.ai.ProjectPlanRequest;
import com.hrm.dto.ai.ProjectPlanResponse;
import com.hrm.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/ai", "/ai"})
@RequiredArgsConstructor
@Validated
@Tag(name = "AI Assistance", description = "AI-powered automation and smart project planning APIs")
public class AiController {

    private final AiService aiService;

    @PostMapping("/project-plan")
    @Operation(summary = "Generate AI Project Plan", description = "Automatically generates breakdown phases, suggested tasks, dependency graph, and risk assessments for a project")
    public ResponseEntity<ApiResponse<ProjectPlanResponse>> generateProjectPlan(
            @Valid @RequestBody ProjectPlanRequest request) {
        ProjectPlanResponse response = aiService.generateProjectPlan(request);
        return ResponseEntity.ok(ApiResponse.success("AI project plan generated successfully", response));
    }
}
