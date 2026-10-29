package com.hrm.config;

import com.hrm.entity.*;
import com.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository commentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Database is empty. Initializing sample data for Team-Project-Task System...");

            String defaultPass = passwordEncoder.encode("123456");

            // 1. Departments
            Department itDept = Department.builder()
                    .name("Phòng Công nghệ Thông tin")
                    .description("Phát triển phần mềm, quản trị hạ tầng và an toàn thông tin")
                    .build();
            itDept = departmentRepository.save(itDept);

            Department salesDept = Department.builder()
                    .name("Phòng Kinh doanh & Tiếp thị")
                    .description("Phát triển thị trường, kinh doanh và quan hệ đối tác")
                    .build();
            salesDept = departmentRepository.save(salesDept);

            Department hrDept = Department.builder()
                    .name("Phòng Nhân sự")
                    .description("Tuyển dụng, đào tạo và chính sách nhân sự")
                    .build();
            hrDept = departmentRepository.save(hrDept);

            // 2. Users
            User admin = User.builder()
                    .fullName("Admin Hệ Thống")
                    .email("admin@hrm.com")
                    .passwordHash(defaultPass)
                    .systemRole("ADMIN")
                    .department(itDept)
                    .build();
            admin = userRepository.save(admin);

            User itManager = User.builder()
                    .fullName("Nguyễn Văn An (IT Manager)")
                    .email("it.manager@hrm.com")
                    .passwordHash(defaultPass)
                    .systemRole("MANAGER")
                    .department(itDept)
                    .build();
            itManager = userRepository.save(itManager);
            itDept.setManager(itManager);
            departmentRepository.save(itDept);

            User salesManager = User.builder()
                    .fullName("Trần Thị Bình (Sales Manager)")
                    .email("sales.manager@hrm.com")
                    .passwordHash(defaultPass)
                    .systemRole("MANAGER")
                    .department(salesDept)
                    .build();
            salesManager = userRepository.save(salesManager);
            salesDept.setManager(salesManager);
            departmentRepository.save(salesDept);

            User dev1 = User.builder()
                    .fullName("Lê Văn Cường (Senior Dev)")
                    .email("dev1@hrm.com")
                    .passwordHash(defaultPass)
                    .systemRole("EMPLOYEE")
                    .department(itDept)
                    .build();
            dev1 = userRepository.save(dev1);

            User dev2 = User.builder()
                    .fullName("Phạm Minh Đức (Backend Dev)")
                    .email("dev2@hrm.com")
                    .passwordHash(defaultPass)
                    .systemRole("EMPLOYEE")
                    .department(itDept)
                    .build();
            dev2 = userRepository.save(dev2);

            User salesEmp = User.builder()
                    .fullName("Hoàng Thị Mai (Sales Specialist)")
                    .email("sales1@hrm.com")
                    .passwordHash(defaultPass)
                    .systemRole("EMPLOYEE")
                    .department(salesDept)
                    .build();
            salesEmp = userRepository.save(salesEmp);

            // 3. Sample Project
            Project project = Project.builder()
                    .name("Phát triển Hệ thống Quản lý Công việc Doanh nghiệp")
                    .description("Dự án xây dựng nền tảng quản lý theo mô hình Team - Project - Task")
                    .leader(itManager)
                    .status("IN_PROGRESS")
                    .startDate(LocalDateTime.now().minusDays(10))
                    .endDate(LocalDateTime.now().plusMonths(2))
                    .createdBy(itManager)
                    .build();
            project = projectRepository.save(project);

            // 4. Project Members
            ProjectMember pmLeader = ProjectMember.builder()
                    .id(new ProjectMemberId(project.getId(), itManager.getId()))
                    .project(project)
                    .user(itManager)
                    .projectRole("PROJECT_LEADER")
                    .joinedAt(LocalDateTime.now().minusDays(10))
                    .build();

            ProjectMember pmManager = ProjectMember.builder()
                    .id(new ProjectMemberId(project.getId(), salesManager.getId()))
                    .project(project)
                    .user(salesManager)
                    .projectRole("PROJECT_MANAGER")
                    .joinedAt(LocalDateTime.now().minusDays(9))
                    .build();

            ProjectMember pmDev1 = ProjectMember.builder()
                    .id(new ProjectMemberId(project.getId(), dev1.getId()))
                    .project(project)
                    .user(dev1)
                    .projectRole("PROJECT_MEMBER")
                    .joinedAt(LocalDateTime.now().minusDays(8))
                    .build();

            ProjectMember pmDev2 = ProjectMember.builder()
                    .id(new ProjectMemberId(project.getId(), dev2.getId()))
                    .project(project)
                    .user(dev2)
                    .projectRole("PROJECT_MEMBER")
                    .joinedAt(LocalDateTime.now().minusDays(8))
                    .build();

            projectMemberRepository.saveAll(List.of(pmLeader, pmManager, pmDev1, pmDev2));

            // 5. Sample Tasks
            Task task1 = Task.builder()
                    .project(project)
                    .title("Thiết kế kiến trúc CSDL và sơ đồ ERD")
                    .description("Hoàn thiện sơ đồ 10 bảng thực thể cho mô hình Team - Project - Task")
                    .priority("HIGH")
                    .status("COMPLETED")
                    .assignee(dev1)
                    .createdBy(itManager)
                    .deadline(LocalDateTime.now().minusDays(3))
                    .build();
            task1 = taskRepository.save(task1);

            Task task2 = Task.builder()
                    .project(project)
                    .title("Xây dựng toàn bộ REST APIs Backend (Spring Boot)")
                    .description("Viết các controller, service, repository và kiểm thử bảo mật JWT")
                    .priority("URGENT")
                    .status("IN_PROGRESS")
                    .assignee(dev2)
                    .createdBy(itManager)
                    .deadline(LocalDateTime.now().plusDays(5))
                    .dependencies(new HashSet<>(Set.of(task1)))
                    .followers(new HashSet<>(Set.of(itManager, dev1)))
                    .build();
            task2 = taskRepository.save(task2);

            Task task3 = Task.builder()
                    .project(project)
                    .title("Tích hợp giao diện Frontend và kiểm thử người dùng")
                    .description("Kết nối các REST APIs từ Backend lên giao diện Web")
                    .priority("MEDIUM")
                    .status("TODO")
                    .assignee(dev1)
                    .createdBy(itManager)
                    .deadline(LocalDateTime.now().plusDays(15))
                    .dependencies(new HashSet<>(Set.of(task2)))
                    .build();
            task3 = taskRepository.save(task3);

            // 6. Comments
            TaskComment comment1 = TaskComment.builder()
                    .task(task2)
                    .user(itManager)
                    .content("Đã hoàn thành Task 1, bạn có thể triển khai tiếp Task 2 nhé!")
                    .build();
            TaskComment comment2 = TaskComment.builder()
                    .task(task2)
                    .user(dev2)
                    .content("Vâng anh, em đang viết phần phân quyền và Swagger docs.")
                    .build();
            commentRepository.saveAll(List.of(comment1, comment2));

            // 7. Activity Logs
            ActivityLog log1 = ActivityLog.builder()
                    .entityType("TASK")
                    .entityId(task1.getId())
                    .action("STATUS_CHANGED")
                    .oldValue("IN_PROGRESS")
                    .newValue("COMPLETED")
                    .performedBy(dev1)
                    .build();
            ActivityLog log2 = ActivityLog.builder()
                    .entityType("TASK")
                    .entityId(task2.getId())
                    .action("STATUS_CHANGED")
                    .oldValue("TODO")
                    .newValue("IN_PROGRESS")
                    .performedBy(dev2)
                    .build();
            activityLogRepository.saveAll(List.of(log1, log2));

            log.info("Sample data initialized successfully!");
            log.info("Default Accounts (Password for all: 123456):");
            log.info(" -> Admin: admin@hrm.com");
            log.info(" -> IT Manager (Project Leader): it.manager@hrm.com");
            log.info(" -> Sales Manager: sales.manager@hrm.com");
            log.info(" -> Developer: dev1@hrm.com, dev2@hrm.com");
        }
    }
}
