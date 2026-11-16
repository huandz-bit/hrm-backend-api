package com.hrm.service;

import com.hrm.dto.profile.EmployeeProfileRequest;
import com.hrm.dto.profile.EmployeeProfileResponse;
import com.hrm.entity.Department;
import com.hrm.entity.EmployeeProfile;
import com.hrm.entity.User;
import com.hrm.exception.BadRequestException;
import com.hrm.exception.ResourceNotFoundException;
import com.hrm.repository.EmployeeProfileRepository;
import com.hrm.repository.UserRepository;
import com.hrm.security.UserPrincipal;
import com.hrm.service.impl.EmployeeProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeProfileRepository employeeProfileRepository;

    @InjectMocks
    private EmployeeProfileServiceImpl profileService;

    private User sampleUser;
    private Department sampleDept;
    private UserPrincipal adminPrincipal;
    private UserPrincipal employeePrincipal;
    private UserPrincipal anotherEmployeePrincipal;

    @BeforeEach
    void setUp() {
        sampleDept = Department.builder()
                .id(10L)
                .name("Phòng Kỹ Thuật")
                .build();

        sampleUser = User.builder()
                .id(1L)
                .fullName("Nguyễn Văn A")
                .email("nva@hrm.com")
                .department(sampleDept)
                .systemRole("EMPLOYEE")
                .status("ACTIVE")
                .build();

        adminPrincipal = UserPrincipal.builder()
                .id(99L)
                .email("admin@hrm.com")
                .systemRole("ADMIN")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        employeePrincipal = UserPrincipal.builder()
                .id(1L)
                .email("nva@hrm.com")
                .systemRole("EMPLOYEE")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                .build();

        anotherEmployeePrincipal = UserPrincipal.builder()
                .id(2L)
                .email("nvb@hrm.com")
                .systemRole("EMPLOYEE")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                .build();
    }

    @Test
    void getProfile_whenProfileNotExists_returnsUserWithNullProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(employeeProfileRepository.findById(1L)).thenReturn(Optional.empty());

        EmployeeProfileResponse response = profileService.getProfile(1L, employeePrincipal);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Nguyễn Văn A", response.getFullName());
        assertEquals("Phòng Kỹ Thuật", response.getDepartmentName());
        assertNull(response.getProfile());
    }

    @Test
    void getProfile_whenProfileExists_returnsFullCombinedData() {
        EmployeeProfile existingProfile = EmployeeProfile.builder()
                .userId(1L)
                .user(sampleUser)
                .phone("0987654321")
                .address("Hà Nội")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .gender("MALE")
                .skills(List.of("Java", "PostgreSQL"))
                .notes(Map.of("bio", "Developer"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(employeeProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));

        EmployeeProfileResponse response = profileService.getProfile(1L, employeePrincipal);

        assertNotNull(response);
        assertNotNull(response.getProfile());
        assertEquals("0987654321", response.getProfile().getPhone());
        assertEquals("Hà Nội", response.getProfile().getAddress());
        assertEquals(2, response.getProfile().getSkills().size());
        assertEquals("Developer", response.getProfile().getNotes().get("bio"));
    }

    @Test
    void getProfile_adminAccessAnotherUser_allowed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(employeeProfileRepository.findById(1L)).thenReturn(Optional.empty());

        EmployeeProfileResponse response = profileService.getProfile(1L, adminPrincipal);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getProfile_employeeAccessAnotherUser_throwsBadRequestException() {
        assertThrows(BadRequestException.class, () -> profileService.getProfile(1L, anotherEmployeePrincipal));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void updateProfile_whenProfileNotExists_createsNewProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(employeeProfileRepository.findById(1L)).thenReturn(Optional.empty());
        when(employeeProfileRepository.save(any(EmployeeProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeProfileRequest request = EmployeeProfileRequest.builder()
                .phone("0912345678")
                .address("Đà Nẵng")
                .skills(List.of("React", "TypeScript"))
                .build();

        EmployeeProfileResponse response = profileService.updateProfile(1L, request, employeePrincipal);

        assertNotNull(response);
        assertNotNull(response.getProfile());
        assertEquals("0912345678", response.getProfile().getPhone());
        assertEquals("Đà Nẵng", response.getProfile().getAddress());
        assertEquals(2, response.getProfile().getSkills().size());
        verify(employeeProfileRepository, times(1)).save(any(EmployeeProfile.class));
    }

    @Test
    void updateProfile_whenUserNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserPrincipal selfPrincipal = UserPrincipal.builder()
                .id(999L)
                .systemRole("EMPLOYEE")
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                profileService.updateProfile(999L, new EmployeeProfileRequest(), selfPrincipal)
        );
    }
}
