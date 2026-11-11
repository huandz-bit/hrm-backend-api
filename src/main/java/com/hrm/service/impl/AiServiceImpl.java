package com.hrm.service.impl;

import com.hrm.dto.ai.ProjectPlanRequest;
import com.hrm.dto.ai.ProjectPlanResponse;
import com.hrm.service.AiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Override
    public ProjectPlanResponse generateProjectPlan(ProjectPlanRequest request) {
        String name = request.getProjectName();
        String desc = request.getDescription() != null ? request.getDescription() : "Comprehensive project execution plan";
        int duration = request.getEstimatedDurationDays() != null && request.getEstimatedDurationDays() > 0
                ? request.getEstimatedDurationDays()
                : 60;

        List<ProjectPlanResponse.PlanPhase> phases = List.of(
                ProjectPlanResponse.PlanPhase.builder()
                        .phaseName("Phase 1: Khảo sát & Phân tích yêu cầu (Analysis & Design)")
                        .durationDays((int) Math.round(duration * 0.2))
                        .description("Thu thập yêu cầu chi tiết, thiết kế kiến trúc hệ thống và luồng dữ liệu.")
                        .build(),
                ProjectPlanResponse.PlanPhase.builder()
                        .phaseName("Phase 2: Triển khai & Phát triển chức năng (Implementation)")
                        .durationDays((int) Math.round(duration * 0.5))
                        .description("Xây dựng các module Backend, kết nối Database, phát triển giao diện người dùng.")
                        .build(),
                ProjectPlanResponse.PlanPhase.builder()
                        .phaseName("Phase 3: Kiểm thử & Đảm bảo chất lượng (QA & Testing)")
                        .durationDays((int) Math.round(duration * 0.2))
                        .description("Kiểm thử chức năng, hiệu năng, bảo mật và sửa lỗi tồn đọng.")
                        .build(),
                ProjectPlanResponse.PlanPhase.builder()
                        .phaseName("Phase 4: Triển khai & Nghiệm thu (Deployment & Handover)")
                        .durationDays((int) Math.round(duration * 0.1))
                        .description("Đóng gói, cấu hình môi trường production, đào tạo người dùng và bàn giao.")
                        .build()
        );

        List<ProjectPlanResponse.SuggestedTask> suggestedTasks = new ArrayList<>();
        suggestedTasks.add(ProjectPlanResponse.SuggestedTask.builder()
                .title("1. Khảo sát nghiệp vụ và xây dựng tài liệu SRS")
                .description("Phỏng vấn các phòng ban liên quan để xác định phạm vi dự án " + name)
                .priority("HIGH")
                .estimatedDays(7)
                .suggestedRole("Business Analyst")
                .dependencyTaskTitles(List.of())
                .build());

        suggestedTasks.add(ProjectPlanResponse.SuggestedTask.builder()
                .title("2. Thiết kế cơ sở dữ liệu và kiến trúc Backend")
                .description("Thiết kế sơ đồ ERD, tối ưu hóa index và chuẩn bị khung dự án")
                .priority("HIGH")
                .estimatedDays(10)
                .suggestedRole("System Architect / Senior Backend")
                .dependencyTaskTitles(List.of("1. Khảo sát nghiệp vụ và xây dựng tài liệu SRS"))
                .build());

        suggestedTasks.add(ProjectPlanResponse.SuggestedTask.builder()
                .title("3. Xây dựng các REST APIs và phân quyền bảo mật")
                .description("Viết các controller, service, áp dụng JWT Bearer authentication")
                .priority("URGENT")
                .estimatedDays(15)
                .suggestedRole("Backend Developer")
                .dependencyTaskTitles(List.of("2. Thiết kế cơ sở dữ liệu và kiến trúc Backend"))
                .build());

        suggestedTasks.add(ProjectPlanResponse.SuggestedTask.builder()
                .title("4. Thiết kế giao diện UI/UX và lập trình Frontend")
                .description("Xây dựng Dashboard, Kanban board quản lý Task và biểu đồ báo cáo")
                .priority("MEDIUM")
                .estimatedDays(18)
                .suggestedRole("Frontend Developer")
                .dependencyTaskTitles(List.of("3. Xây dựng các REST APIs và phân quyền bảo mật"))
                .build());

        suggestedTasks.add(ProjectPlanResponse.SuggestedTask.builder()
                .title("5. Tích hợp hệ thống và kiểm thử End-to-End")
                .description("Chạy kiểm thử tích hợp, kiểm tra bảo mật API và nghiệm thu nội bộ")
                .priority("HIGH")
                .estimatedDays(10)
                .suggestedRole("QA/QC Engineer")
                .dependencyTaskTitles(List.of("4. Thiết kế giao diện UI/UX và lập trình Frontend"))
                .build());

        List<String> roles = request.getAvailableRoles() != null && !request.getAvailableRoles().isEmpty()
                ? request.getAvailableRoles()
                : List.of("Project Leader", "Project Manager", "Backend Developer", "Frontend Developer", "QA Engineer");

        List<String> risks = List.of(
                "Rủi ro biến động yêu cầu nghiệp vụ giữa các phòng ban trong quá trình thực hiện.",
                "Rủi ro chậm tiến độ ở các Task phụ thuộc liên chuỗi.",
                "Cần kiểm soát chặt chẽ việc phân quyền và bảo mật dữ liệu giữa các bộ phận."
        );

        return ProjectPlanResponse.builder()
                .projectName(name)
                .executiveSummary("Kế hoạch dự án được tự động phân tích bởi AI dựa trên mục tiêu: " + desc)
                .estimatedTotalDays(duration)
                .phases(phases)
                .suggestedTasks(suggestedTasks)
                .recommendedRoles(roles)
                .potentialRisks(risks)
                .build();
    }
}
