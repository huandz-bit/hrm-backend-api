package com.hrm.dto.profile;
import lombok.*; import java.time.LocalDateTime; import java.util.Map;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor public class ProfileChangeRequestResponse { private Long id; private Long requesterId; private String requesterName; private String requesterEmail; private String requesterDepartmentName; private String status; private Map<String,Object> requestedData; private String reviewNote; private LocalDateTime createdAt; private LocalDateTime reviewedAt; }
