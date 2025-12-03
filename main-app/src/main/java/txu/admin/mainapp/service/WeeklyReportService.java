package txu.admin.mainapp.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import txu.admin.mainapp.dao.DepartmentDao;
import txu.admin.mainapp.dao.WeeklyReportDao;
import txu.admin.mainapp.dto.DepartmentDto;
import txu.admin.mainapp.dto.LinkDto;
import txu.admin.mainapp.dto.UploadfileInfoRequest;
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

    @Value("${ceph.rgw.bucket}")
    private String bucketName;

    @Value("${ceph.rgw.url}")
    private String url;
    public LinkDto getPreSignedUrlForGet(String filename) throws Exception {

        String pre_signed_url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(filename)
                        .expiry(60)    // seconds
                        .build()
        );
        LinkDto linkDto = new LinkDto();
        linkDto.setPre_signed_url(pre_signed_url);
        return linkDto;
    }

    public LinkDto getPreSignedUrlForPut(String filename) throws Exception {
        String filename_ = UUID.randomUUID() + "_" + filename;

        String pre_signed_url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(filename_)
                        .method(Method.PUT)
                        .expiry(500) // 10 phút
                        .build()
        );

        LinkDto linkDto = new LinkDto();
        linkDto.setPre_signed_url(pre_signed_url);
        linkDto.setFilename(filename_);
        return linkDto;
    }

    public WeeklyReportEntity addReport(UploadfileInfoRequest request) throws Exception {

        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder.
        // Việc lấy thông tin này ch yếu để xác định người dùng hiện tại đang ở phòng ban nào, để cập nhật hoặc tạo báo cáo cho phòng ban đó.
        // Ở đây không xử lý xác thực người dung, vì việc này đã được thực hiện bở kong gateway
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                userDetails = (CustomUserDetails) principal;
//                String username = userDetails.getUsername();
//                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            } else {
                userDetails = null;
            }
        } else {
            userDetails = null;
        }

        // Nếu tồn tại những thông tin report trong tuần mà liên qua đến người dùng (thuộc phòng ban) đã upload report hiện tại thì
        // xóa hết report đã upload trên lên storage1 (ngoại trừ file báo cáo hiện tại), và xóa tất cả dữ liệu lưu ở cơ sở dữ liệu (trong tuần hiện tại)
        List<WeeklyReportEntity> weeklyReportEntities = weeklyReportDao.getFromDateToDate(toDate(getStartOfWeek()), toDate(getEndOfWeek()));
        weeklyReportEntities.forEach(weeklyReportEntity -> {
            if (weeklyReportEntity.getDepartment().getId() == userDetails.getDepartment_id()) {

                if(weeklyReportEntity.getFilename() != request.getFilename()){
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
                }

                // Xóa dữ liệu
                weeklyReportDao.remove(weeklyReportEntity);
            }
        });

        // Thêm kiểm tra file báo cáo có tồn tại trên bucket chưa, nếu chưa thì không cập nhật dữ liệu

        String fileUrl = String.format( url + "/%s/%s", bucketName, request.getFilename());
        // Save metadata
        DepartmentEntity department = null;
        if (userDetails != null) {
            department = departmentDao.findById(userDetails.getDepartment_id());
        }

        WeeklyReportEntity weeklyReport = new WeeklyReportEntity();
        weeklyReport.setFilename(request.getFilename());
        weeklyReport.setUrl(fileUrl);
        weeklyReport.setOriginName(request.getFilenameOrigin());
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
