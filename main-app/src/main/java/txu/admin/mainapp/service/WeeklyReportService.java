package txu.admin.mainapp.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import txu.admin.mainapp.dao.DepartmentDao;
import txu.admin.mainapp.dao.WeeklyReportDao;
import txu.admin.mainapp.dto.DepartmentDto;
import txu.admin.mainapp.entity.DepartmentEntity;
import txu.admin.mainapp.entity.WeeklyReportEntity;
import txu.admin.mainapp.security.CustomUserDetails;
import txu.common.exception.NotFoundException;

import java.util.*;

import static txu.admin.mainapp.common.DateUtil.*;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final MinioClient minioClient;
    private final WeeklyReportDao weeklyReportDao;
    private final DepartmentDao departmentDao;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.url}")
    private String url;

    public WeeklyReportEntity create(MultipartFile file) throws Exception {

        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                userDetails = (CustomUserDetails) principal;
                String username = userDetails.getUsername();
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            } else {
                userDetails = null;
            }
        } else {
            userDetails = null;
        }

        // Nếu tồn tại những thông tin report trong tuần mà liên qua đến người dùng đang upload report hiện tại thì
        // xóa hết report đã upload trên minio và xóa hết dữ liệu lưu ở cơ sở dữ liệu (trong tuần hiện tại)
        List<WeeklyReportEntity> weeklyReportEntities = weeklyReportDao.getFromDateToDate(toDate(getStartOfWeek()), toDate(getEndOfWeek()));
        weeklyReportEntities.forEach(weeklyReportEntity -> {
            if (weeklyReportEntity.getDepartment().getId() == userDetails.getDepartment_id()) {
                // Xóa file trên minio
                try {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(weeklyReportEntity.getFilename())
                                    .build()
                    );
                    System.out.println("Deleted successfully: " + weeklyReportEntity.getFilename());
                } catch (Exception e) {
                    System.err.println("Error deleting file: " + e.getMessage());
                    throw new RuntimeException("File deletion failed", e);
                }
                // Xóa dữ liệu
                weeklyReportDao.remove(weeklyReportEntity);
            }
        });


        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Ensure bucket exists
        boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        // Upload to MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filename)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        String fileUrl = String.format( url + "/%s/%s", bucketName, filename);

        // Save metadata
        DepartmentEntity department = null;
        if (userDetails != null) {
            department = departmentDao.findById(userDetails.getDepartment_id());
        }

        WeeklyReportEntity weeklyReport = new WeeklyReportEntity();
        weeklyReport.setFilename(filename);
        weeklyReport.setUrl(fileUrl);
        weeklyReport.setOriginName(file.getOriginalFilename());
        weeklyReport.setDepartment(department);
        weeklyReport.setUploadedAt(DateTime.now().toDate());
        return weeklyReportDao.save(weeklyReport);
    }

    public List<WeeklyReportEntity> getWithLimit(int limit) {
        return weeklyReportDao.getWithLimit(limit);
    }

    public List<WeeklyReportEntity> getFromDateToDate(Date from, Date to) {
        return weeklyReportDao.getFromDateToDate(from, to);
    }

    public List<DepartmentDto> getNoReportedFromDateToDate(Date from, Date to) {
        List<DepartmentDto> departmentNoReport = new ArrayList<DepartmentDto>();
        ArrayList<Object> departmentsReport = new ArrayList<>();

        List<WeeklyReportEntity> uploadFileEntities = weeklyReportDao.getFromDateToDate(from, to);
        uploadFileEntities.forEach(weeklyReportEntity -> {
            departmentsReport.add(weeklyReportEntity.getDepartment().getId());
        });

        List<DepartmentEntity> departmentEntities = departmentDao.getWithLimit(1000);
        departmentEntities.forEach(department -> {
            if (!departmentsReport.contains(department.getId())) {
                DepartmentDto dpm = new DepartmentDto();
                dpm.setId(department.getId());
                dpm.setName(department.getName());
                departmentNoReport.add(dpm);
            }
        });
        return departmentNoReport;
    }

    public WeeklyReportEntity getById(int id) {

        return weeklyReportDao.findById(id);
    }

    public boolean removeById(int id) {
        WeeklyReportEntity weeklyReport = weeklyReportDao.findById(id);
        if (weeklyReport == null) {
            throw new NotFoundException("Department is not found");
        }
        weeklyReportDao.remove(weeklyReport);
        return true;
    }


}
