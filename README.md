# Enterprise Work & Project Management Backend (Team - Project - Task)

Hệ thống Backend REST API phục vụ nền tảng Quản lý Công việc & Dự án Doanh nghiệp 3 tầng (**Team – Project – Task**) với đầy đủ các API dành cho Frontend: **Xác thực, Dashboard, Phòng ban, Người dùng, Dự án, Thành viên dự án, Công việc (Task), Đánh giá Nhân sự (HR Scorecards), Cấu hình Email, và Trí tuệ nhân tạo (AI)**.

> 💡 **Lưu ý định tuyến cho Frontend**: Tất cả các API đều hỗ trợ song song 2 định dạng đường dẫn:
> - Có tiền tố: `/api/...` (ví dụ: `/api/auth/login`, `/api/tasks`, `/api/hr/scorecards`)
> - Không có tiền tố: `/...` (ví dụ: `/auth/login`, `/tasks`, `/hr/scorecards`)

---

## 🛠 Công Nghệ Sử Dụng

- **Ngôn ngữ**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Cơ sở dữ liệu**: PostgreSQL
- **ORM / Database**: Spring Data JPA / Hibernate
- **Bảo mật**: Spring Security 6 + JWT (jjwt 0.12.5)
- **Tài liệu API**: OpenAPI 3 / Swagger UI (`springdoc-openapi`)
- **Khác**: Lombok, BCrypt Password Hashing, CORS Enabled cho mọi domain FE

---

## 🚀 Hướng Dẫn Khởi Chạy

### 1. Cơ sở dữ liệu PostgreSQL
Tạo cơ sở dữ liệu `hrm_db`:
```sql
CREATE DATABASE hrm_db;
```
*(Cấu hình kết nối tại `src/main/resources/application.yml`)*

### 2. Chạy ứng dụng
Mở project bằng IntelliJ IDEA / Eclipse hoặc chạy terminal:
```bash
mvn clean spring-boot:run
```

Hệ thống sẽ tự động tạo bảng và nạp sẵn dữ liệu mẫu (`DataInitializer`):
- **Mật khẩu chung cho tất cả tài khoản mẫu**: `123456`
- **Tài khoản mẫu**:
  - Admin: `admin@hrm.com`
  - IT Manager (Project Leader): `it.manager@hrm.com`
  - Sales Manager (Project Manager): `sales.manager@hrm.com`
  - Developer: `dev1@hrm.com`, `dev2@hrm.com`
  - Sales Employee: `sales1@hrm.com`

### 3. Swagger UI
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
👉 OpenAPI JSON: `http://localhost:8080/api-docs`

---

## 📡 Danh Sách Các REST APIs Chi Tiết Cho Frontend

### 1. Xác thực (Authentication)
| Method | Endpoint | Mô tả | Yêu cầu Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` hoặc `/api/auth/login` | Đăng nhập lấy Bearer JWT token | ❌ Không |
| `GET` | `/auth/me` hoặc `/api/auth/me` | Lấy thông tin user hiện tại đang đăng nhập |  Có |

**Response Đăng nhập mẫu:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "fullName": "Admin Hệ Thống",
    "email": "admin@hrm.com",
    "systemRole": "ADMIN",
    "departmentId": 1
  }
}
```

> **Header gửi từ Frontend**: `Authorization: Bearer <token>`

---

### 2. Thống Kê & Báo Cáo (Dashboard)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/dashboard/overview` | Tổng quan số task theo trạng thái, task quá hạn, số dự án active/completed |
| `GET` | `/dashboard/department-workload` | Báo cáo chi tiết tải công việc theo từng phòng ban và từng nhân viên |

---

### 3. Quản Lý Phòng Ban (Departments / Teams)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/departments?all=true&page=0&size=1000&sort=id,desc` | Danh sách phòng ban (lấy hết hoặc phân trang) |
| `GET` | `/departments/{id}` | Lấy chi tiết phòng ban |
| `POST` | `/departments` | Tạo phòng ban mới (`name`, `description`, `managerId`) |
| `PUT` | `/departments/{id}` | Cập nhật phòng ban & đổi trưởng phòng |
| `DELETE` | `/departments/{id}` | Xóa phòng ban |

---

### 4. Quản Lý Người Dùng & Nhân Sự (Users)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/users?page=0&size=1000&sort=id,desc` | Danh sách nhân sự / người dùng |
| `GET` | `/users/{id}` | Chi tiết người dùng |
| `POST` | `/users` | Thêm tài khoản nhân sự mới |
| `PUT` | `/users/{id}` | Cập nhật thông tin nhân sự |
| `DELETE` | `/users/{id}` | Vô hiệu hóa tài khoản (`status: INACTIVE`) |

---

### 5. Quản Lý Dự Án (Projects)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/projects?page=0&size=1000&sort=id,desc` | Lấy danh sách dự án (kèm % tiến độ, tổng số task) |
| `GET` | `/projects?myProjects=true` | Lấy danh sách dự án mà user hiện tại đang tham gia |
| `GET` | `/projects/{id}` | Chi tiết dự án |
| `POST` | `/projects` | Tạo dự án mới (người tạo tự động làm Leader) |
| `PUT` | `/projects/{id}` | Cập nhật thông tin dự án |
| `PATCH` | `/projects/{id}/close` | Đóng dự án (kiểm tra không còn task chưa xong) |
| `DELETE` | `/projects/{id}` | Hủy dự án (`status: CANCELLED`) |

---

### 6. Thành Viên Dự Án (Project Members)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/projects/{projectId}/members` | Lấy danh sách thành viên dự án |
| `POST` | `/projects/{projectId}/members` | Thêm thành viên vào dự án (`userId`, `projectRole`) |
| `PUT` | `/projects/{projectId}/members/{userId}?projectRole={role}` | Cập nhật vai trò thành viên |
| `DELETE` | `/projects/{projectId}/members/{userId}` | Xóa thành viên khỏi dự án |

---

### 7. Quản Lý Công Việc (Tasks)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/tasks?projectId={projectId}&status={status?}&listOnly=true&page=0&size=1000&sort=id,desc` | Lấy danh sách task của dự án (hỗ trợ Kanban board hoặc dạng bảng) |
| `GET` | `/tasks/my-tasks` | Lấy các task được giao cho user hiện tại |
| `GET` | `/tasks/my-following` | Lấy các task user hiện tại đang follow |
| `GET` | `/tasks/{id}` | Chi tiết task (kèm assignee, dependencies, followers) |
| `POST` | `/tasks` | Tạo task mới (chống giao chéo ban cho Project Manager) |
| `PUT` | `/tasks/{id}` | Cập nhật thông tin task |
| `PATCH` | `/tasks/{taskId}/assign` | Giao hoặc chuyển giao task cho nhân viên khác |
| `PATCH` | `/tasks/{taskId}/status` | Cập nhật trạng thái task (`TODO`, `IN_PROGRESS`, `REVIEW`, `COMPLETED`) |
| `POST` | `/tasks/{id}/followers/{userId}` | Thêm follower cho task |
| `DELETE` | `/tasks/{id}/followers/{userId}` | Xóa follower khỏi task |
| `POST` | `/tasks/{id}/dependencies/{dependsOnTaskId}` | Gán task phụ thuộc |
| `DELETE` | `/tasks/{id}/dependencies/{dependsOnTaskId}` | Xóa task phụ thuộc |
| `DELETE` | `/tasks/{id}` | Xóa task |

---

### 8. Đánh Giá Nhân Sự (HR Scorecards)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/hr/scorecards` hoặc `/api/hr/scorecards` | Lấy bảng đánh giá hiệu suất nhân sự (tổng task, tỉ lệ hoàn thành, task quá hạn, điểm hiệu năng, danh hiệu/badges) |
| `GET` | `/hr/scorecards?departmentId={id}` | Lọc đánh giá theo phòng ban |

**Dữ liệu trả về mẫu:**
```json
{
  "success": true,
  "message": "HR scorecards retrieved successfully",
  "data": [
    {
      "userId": 4,
      "fullName": "Lê Văn Cường (Senior Dev)",
      "email": "dev1@hrm.com",
      "departmentId": 1,
      "departmentName": "Phòng Công nghệ Thông tin",
      "systemRole": "EMPLOYEE",
      "totalAssignedTasks": 4,
      "completedTasks": 3,
      "inProgressTasks": 1,
      "overdueTasks": 0,
      "completionRate": 75.0,
      "performanceScore": 87.5,
      "skillsOrBadges": ["On-Time Delivery", "Key Contributor"]
    }
  ]
}
```

---

### 9. Cấu Hình Email (Email Configuration)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/email-config` hoặc `/api/email-config` | Lấy cấu hình SMTP email của hệ thống |
| `PUT` | `/email-config` hoặc `/api/email-config` | Cập nhật cấu hình SMTP email & cài đặt nhắc việc |

**Body cập nhật mẫu (`PUT /email-config`):**
```json
{
  "smtpHost": "smtp.gmail.com",
  "smtpPort": 587,
  "senderEmail": "notifications@hrm-enterprise.com",
  "senderName": "HRM System Notification",
  "username": "notifications@hrm-enterprise.com",
  "password": "app-password-here",
  "enableSsl": false,
  "enableTls": true,
  "taskNotificationEnabled": true,
  "deadlineReminderEnabled": true
}
```

---

### 10. Trí Tuệ Nhân Tạo (AI Project Planning)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/ai/project-plan` hoặc `/api/ai/project-plan` | Phân tích tự động & lập kế hoạch dự án chi tiết bằng AI |

**Body yêu cầu mẫu (`POST /ai/project-plan`):**
```json
{
  "projectName": "Xây dựng cổng thanh toán điện tử",
  "description": "Tích hợp cổng thanh toán VNPay và Momo cho ứng dụng thương mại điện tử",
  "estimatedDurationDays": 45,
  "availableRoles": ["Project Leader", "Backend Developer", "Frontend Developer", "QA Engineer"]
}
```

**Dữ liệu AI sinh ra trả về:**
- `phases`: Các giai đoạn (Khảo sát, Phát triển, Testing, Triển khai) kèm số ngày.
- `suggestedTasks`: Danh sách các đầu việc gợi ý kèm độ ưu tiên, vai trò thực hiện và quan hệ phụ thuộc.
- `potentialRisks`: Đánh giá các rủi ro tiềm ẩn của dự án.
- `recommendedRoles`: Đề xuất nhân sự cho dự án.

---

### 11. Thảo Luận & Tệp Đính Kèm (Comments & Attachments)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/tasks/{taskId}/comments` | Danh sách bình luận trong task |
| `POST` | `/tasks/{taskId}/comments` | Thêm bình luận |
| `DELETE` | `/tasks/{taskId}/comments/{commentId}` | Xóa bình luận |
| `GET` | `/attachments?entityType=TASK&entityId={id}` | Danh sách tệp đính kèm |
| `POST` | `/attachments/upload` (multipart/form-data) | Upload tệp đính kèm |
| `GET` | `/attachments/download/{id}` | Tải file đính kèm |
| `GET` | `/activity-logs?entityType=TASK&entityId={id}` | Lịch sử thay đổi task |
