# Enterprise Work & Project Management Backend (Team - Project - Task)

Hệ thống Backend REST API phục vụ nền tảng Quản lý Công việc & Dự án Doanh nghiệp 3 tầng (**Team – Project – Task**) được thiết kế và hiện thực hóa chính xác theo sơ đồ CSDL ERD (10 bảng) và tài liệu đặc tả nghiệp vụ hệ thống.

---

## 🛠 Công Nghệ Sử Dụng

- **Ngôn ngữ**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Cơ sở dữ liệu**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Bảo mật**: Spring Security 6 + JWT (jjwt 0.12.5)
- **Tài liệu API**: OpenAPI 3 / Swagger UI (`springdoc-openapi`)
- **Khác**: Lombok, BCrypt Password Hashing, CORS Enabled

---

## 📊 Mô Hình 10 Bảng Cơ Sở Dữ Liệu (Chuẩn ERD)

1. **`departments`**: Phòng ban (`id`, `name`, `description`, `manager_id`, `created_at`, `updated_at`)
2. **`users`**: Người dùng / Nhân viên (`id`, `full_name`, `email`, `password_hash`, `department_id`, `system_role`, `status`, `created_at`, `updated_at`)
3. **`projects`**: Dự án (`id`, `name`, `description`, `leader_id`, `status`, `start_date`, `end_date`, `created_by`, `created_at`, `updated_at`)
4. **`project_members`**: Thành viên dự án (`project_id`, `user_id`, `project_role`, `joined_at`)
5. **`tasks`**: Công việc (`id`, `project_id`, `title`, `description`, `priority`, `status`, `assignee_id`, `created_by`, `deadline`, `created_at`, `updated_at`)
6. **`task_dependencies`**: Công việc phụ thuộc (`task_id`, `depends_on_task_id`)
7. **`task_followers`**: Người theo dõi công việc (`task_id`, `user_id`)
8. **`task_comments`**: Bình luận / trao đổi công việc (`id`, `task_id`, `user_id`, `content`, `created_at`)
9. **`attachments`**: Tệp đính kèm theo Task/Project (`id`, `entity_type`, `entity_id`, `file_url`, `file_name`, `file_size`, `uploaded_by`, `created_at`)
10. **`activity_logs`**: Nhật ký hoạt động / Audit trail (`id`, `entity_type`, `entity_id`, `action`, `old_value`, `new_value`, `performed_by`, `created_at`)

---

## 🔐 Hệ Thống Phân Quyền Ma Trận (Matrix Permissions)

### 1. Vai trò cấp doanh nghiệp (`system_role`):
- **`ADMIN`**: Toàn quyền quản trị tài khoản, phòng ban, cấu hình hệ thống.
- **`MANAGER`**: Quản lý phòng ban (Trưởng phòng), thành viên trong phòng ban và dự án tham gia.
- **`EMPLOYEE`**: Nhân viên trực thuộc phòng ban.

### 2. Vai trò trong từng dự án (`project_role`):
- **`PROJECT_LEADER`**: Chịu trách nhiệm tổng thể dự án. Có quyền quản lý toàn bộ Task, bổ nhiệm Project Manager, thêm/xóa thành viên và đóng dự án.
- **`PROJECT_MANAGER`**: Quản lý và giao Task cho nhân viên **thuộc phòng ban của mình** (nghiệp vụ chống giao chéo ban).
- **`PROJECT_MEMBER`**: Xem các Task được phân công, thực hiện, cập nhật tiến độ, bình luận và đính kèm tệp.

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

## 📡 Danh Sách Toàn Bộ REST APIs Cung Cấp Cho Frontend

### 1. Xác thực (Authentication)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Đăng nhập lấy Bearer JWT token |
| `GET` | `/api/auth/me` | Lấy thông tin user đang đăng nhập |

> **Header bắt buộc sau khi đăng nhập**: `Authorization: Bearer <token>`

---

### 2. Quản lý Người dùng & Nhân sự (`/api/users`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/users?page=0&size=10` | Danh sách người dùng (phân trang) |
| `GET` | `/api/users?departmentId={id}` | Lọc danh sách nhân viên theo phòng ban |
| `GET` | `/api/users/{id}` | Chi tiết người dùng theo ID |
| `POST` | `/api/users` | Tạo tài khoản người dùng / nhân viên mới |
| `PUT` | `/api/users/{id}` | Cập nhật thông tin người dùng |
| `DELETE`| `/api/users/{id}` | Vô hiệu hóa người dùng (status -> INACTIVE) |

---

### 3. Quản lý Phòng ban - Team (`/api/departments`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/departments` | Danh sách phòng ban (phân trang) |
| `GET` | `/api/departments?all=true` | Lấy toàn bộ danh sách phòng ban (cho dropdown FE) |
| `GET` | `/api/departments/{id}` | Chi tiết phòng ban |
| `POST` | `/api/departments` | Tạo phòng ban mới (chỉ định manager_id) |
| `PUT` | `/api/departments/{id}` | Cập nhật phòng ban & trưởng phòng |
| `DELETE`| `/api/departments/{id}` | Xóa phòng ban |

---

### 4. Quản lý Dự án (`/api/projects`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/projects` | Danh sách dự án (phân trang, có % tiến độ) |
| `GET` | `/api/projects?myProjects=true`| Danh sách dự án mà người dùng hiện tại tham gia |
| `GET` | `/api/projects/{id}` | Chi tiết dự án |
| `POST` | `/api/projects` | Tạo dự án mới (mặc định gán người tạo làm Leader) |
| `PUT` | `/api/projects/{id}` | Cập nhật thông tin dự án |
| `PATCH` | `/api/projects/{id}/close` | **Đóng dự án** (kiểm tra điều kiện không còn task tồn đọng) |
| `DELETE`| `/api/projects/{id}` | Hủy dự án (status -> CANCELLED) |

---

### 5. Thành viên Dự án (`/api/projects/{id}/members`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/projects/{id}/members` | Danh sách thành viên trong dự án |
| `POST` | `/api/projects/{id}/members` | Thêm thành viên vào dự án (gán role) |
| `PUT` | `/api/projects/{id}/members/{userId}?projectRole={role}` | Cập nhật vai trò thành viên |
| `DELETE`| `/api/projects/{id}/members/{userId}` | Xóa thành viên khỏi dự án |

---

### 6. Quản lý Công việc - Tasks (`/api/tasks`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/tasks?projectId={id}` | Lấy danh sách công việc của dự án (phân trang) |
| `GET` | `/api/tasks?projectId={id}&listOnly=true` | Lấy danh sách task dạng list (cho Board/Gantt) |
| `GET` | `/api/tasks/my-tasks` | Danh sách công việc được giao cho user hiện tại |
| `GET` | `/api/tasks/my-following` | Danh sách công việc user hiện tại đang theo dõi |
| `GET` | `/api/tasks/{id}` | Chi tiết công việc (kèm dependencies & followers) |
| `POST` | `/api/tasks` | Tạo công việc mới (chống giao task chéo ban cho Manager) |
| `PUT` | `/api/tasks/{id}` | Cập nhật thông tin công việc |
| `PATCH` | `/api/tasks/{id}/status` | Cập nhật trạng thái (kiểm tra task phụ thuộc trước khi xong) |
| `PATCH` | `/api/tasks/{id}/assign` | Chuyển giao công việc (1 Assignee duy nhất) |
| `POST` | `/api/tasks/{id}/followers/{userId}` | Thêm người theo dõi/hỗ trợ công việc |
| `DELETE`| `/api/tasks/{id}/followers/{userId}` | Xóa người theo dõi |
| `POST` | `/api/tasks/{id}/dependencies/{dependsOnTaskId}` | Gán quan hệ phụ thuộc (task này chờ task kia) |
| `DELETE`| `/api/tasks/{id}/dependencies/{dependsOnTaskId}` | Xóa quan hệ phụ thuộc |
| `DELETE`| `/api/tasks/{id}` | Xóa công việc |

---

### 7. Thảo luận & Bình luận (`/api/tasks/{taskId}/comments`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/tasks/{taskId}/comments` | Lấy danh sách bình luận trong task |
| `POST` | `/api/tasks/{taskId}/comments` | Đăng bình luận thảo luận |
| `DELETE`| `/api/tasks/{taskId}/comments/{commentId}` | Xóa bình luận |

---

### 8. Tệp đính kèm (`/api/attachments`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/attachments?entityType=TASK&entityId={id}` | Danh sách tệp đính kèm của Task/Project |
| `POST` | `/api/attachments/upload` (multipart/form-data) | Upload tệp đính kèm |
| `GET` | `/api/attachments/download/{id}` | Tải xuống tệp đính kèm |
| `DELETE`| `/api/attachments/{id}` | Xóa tệp đính kèm |

---

### 9. Nhật ký hoạt động (`/api/activity-logs`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/activity-logs?entityType=TASK&entityId={id}` | Xem lịch sử thay đổi của Task (ai đổi trạng thái, assignee, deadline) |
| `GET` | `/api/activity-logs?entityType=PROJECT&entityId={id}`| Dòng thời gian hoạt động của toàn bộ Dự án |

---

### 10. Báo cáo & Thống kê - Dashboard (`/api/dashboard`)
| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard/overview` | Tổng quan số lượng Task theo trạng thái, Task quá hạn, dự án đang chạy |
| `GET` | `/api/dashboard/department-workload` | Báo cáo chi tiết tải công việc theo từng phòng ban và từng nhân viên |
