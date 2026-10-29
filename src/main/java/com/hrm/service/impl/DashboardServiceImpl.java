package com.hrm.service.impl;

import com.hrm.dto.dashboard.DashboardOverviewResponse;
import com.hrm.dto.dashboard.DepartmentWorkloadResponse;
import com.hrm.entity.Department;
import com.hrm.entity.Task;
import com.hrm.entity.User;
import com.hrm.repository.DepartmentRepository;
import com.hrm.repository.ProjectRepository;
import com.hrm.repository.TaskRepository;
import com.hrm.repository.UserRepository;
import com.hrm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardOverviewResponse getOverview() {
        long totalProjects = projectRepository.count();
        long activeProjects = projectRepository.findByStatus("IN_PROGRESS").size()
                + projectRepository.findByStatus("PLANNING").size();
        long completedProjects = projectRepository.findByStatus("COMPLETED").size()
                + projectRepository.findByStatus("CLOSED").size();

        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus("COMPLETED");
        long inProgressTasks = taskRepository.countByStatus("IN_PROGRESS");
        long reviewTasks = taskRepository.countByStatus("REVIEW");
        long todoTasks = taskRepository.countByStatus("TODO");
        long overdueTasks = taskRepository.countOverdueTasks(LocalDateTime.now());

        Map<String, Long> tasksByStatus = new HashMap<>();
        tasksByStatus.put("TODO", todoTasks);
        tasksByStatus.put("IN_PROGRESS", inProgressTasks);
        tasksByStatus.put("REVIEW", reviewTasks);
        tasksByStatus.put("COMPLETED", completedTasks);
        tasksByStatus.put("OVERDUE", overdueTasks);

        return DashboardOverviewResponse.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .completedProjects(completedProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .reviewTasks(reviewTasks)
                .todoTasks(todoTasks)
                .overdueTasks(overdueTasks)
                .tasksByStatus(tasksByStatus)
                .build();
    }

    @Override
    public List<DepartmentWorkloadResponse> getDepartmentWorkload() {
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentWorkloadResponse> result = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Department dept : departments) {
            List<User> employees = userRepository.findByDepartmentId(dept.getId());
            int deptTotalTasks = 0;
            int deptCompletedTasks = 0;

            List<DepartmentWorkloadResponse.EmployeeWorkload> employeeWorkloads = new ArrayList<>();

            for (User emp : employees) {
                List<Task> empTasks = taskRepository.findByAssigneeId(emp.getId());
                int total = empTasks.size();
                int completed = (int) empTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
                int inProgress = (int) empTasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
                int overdue = (int) empTasks.stream().filter(t -> t.getDeadline() != null
                        && t.getDeadline().isBefore(now)
                        && !"COMPLETED".equalsIgnoreCase(t.getStatus())
                        && !"CANCELLED".equalsIgnoreCase(t.getStatus())).count();

                deptTotalTasks += total;
                deptCompletedTasks += completed;

                employeeWorkloads.add(DepartmentWorkloadResponse.EmployeeWorkload.builder()
                        .userId(emp.getId())
                        .fullName(emp.getFullName())
                        .email(emp.getEmail())
                        .totalTasks(total)
                        .completedTasks(completed)
                        .inProgressTasks(inProgress)
                        .overdueTasks(overdue)
                        .build());
            }

            result.add(DepartmentWorkloadResponse.builder()
                    .departmentId(dept.getId())
                    .departmentName(dept.getName())
                    .totalEmployees(employees.size())
                    .totalTasks(deptTotalTasks)
                    .completedTasks(deptCompletedTasks)
                    .pendingTasks(deptTotalTasks - deptCompletedTasks)
                    .employees(employeeWorkloads)
                    .build());
        }

        return result;
    }
}
