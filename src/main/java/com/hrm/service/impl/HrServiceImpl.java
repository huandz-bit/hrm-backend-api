package com.hrm.service.impl;

import com.hrm.dto.hr.HrScorecardResponse;
import com.hrm.entity.Task;
import com.hrm.entity.User;
import com.hrm.repository.TaskRepository;
import com.hrm.repository.UserRepository;
import com.hrm.service.HrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrServiceImpl implements HrService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<HrScorecardResponse> getScorecards(Long departmentId) {
        List<User> users;
        if (departmentId != null) {
            users = userRepository.findByDepartmentId(departmentId);
        } else {
            users = userRepository.findAll();
        }

        LocalDateTime now = LocalDateTime.now();
        List<HrScorecardResponse> scorecards = new ArrayList<>();

        for (User u : users) {
            List<Task> tasks = taskRepository.findByAssigneeId(u.getId());
            int total = tasks.size();
            int completed = (int) tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
            int inProgress = (int) tasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
            int overdue = (int) tasks.stream().filter(t -> t.getDeadline() != null
                    && t.getDeadline().isBefore(now)
                    && !"COMPLETED".equalsIgnoreCase(t.getStatus())
                    && !"CANCELLED".equalsIgnoreCase(t.getStatus())).count();

            double completionRate = total > 0 ? ((double) completed / total) * 100.0 : 0.0;
            // Performance score formula: completion rate penalized by overdue ratio
            double penalty = total > 0 ? ((double) overdue / total) * 30.0 : 0.0;
            double score = Math.max(0.0, Math.min(100.0, (completionRate * 0.9) - penalty + (total > 0 ? 10.0 : 50.0)));

            List<String> badges = new ArrayList<>();
            if (completionRate >= 90.0 && total >= 3) badges.add("Top Performer");
            if (overdue == 0 && total >= 1) badges.add("On-Time Delivery");
            if (total >= 5) badges.add("Key Contributor");

            scorecards.add(HrScorecardResponse.builder()
                    .userId(u.getId())
                    .fullName(u.getFullName())
                    .email(u.getEmail())
                    .departmentId(u.getDepartment() != null ? u.getDepartment().getId() : null)
                    .departmentName(u.getDepartment() != null ? u.getDepartment().getName() : null)
                    .systemRole(u.getSystemRole())
                    .totalAssignedTasks(total)
                    .completedTasks(completed)
                    .inProgressTasks(inProgress)
                    .overdueTasks(overdue)
                    .completionRate(Math.round(completionRate * 10.0) / 10.0)
                    .performanceScore(Math.round(score * 10.0) / 10.0)
                    .skillsOrBadges(badges)
                    .build());
        }

        return scorecards;
    }
}
