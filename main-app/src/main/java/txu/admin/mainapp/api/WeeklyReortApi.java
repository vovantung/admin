package txu.admin.mainapp.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import txu.admin.mainapp.base.AbstractApi;
import txu.admin.mainapp.dto.DepartmentDto;
import txu.admin.mainapp.dto.IdRequest;
import txu.admin.mainapp.dto.FromDateToDateRequest;
import txu.admin.mainapp.dto.LimitRequest;
import txu.admin.mainapp.entity.WeeklyReportEntity;
import txu.admin.mainapp.service.WeeklyReportService;

import java.util.List;

@RestController
@RequestMapping("/weekly-report")
@RequiredArgsConstructor
public class WeeklyReortApi extends AbstractApi {
    private final WeeklyReportService weeklyReportService;

    @PostMapping("/create")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            WeeklyReportEntity weeklyReport = weeklyReportService.create(file);
            return ResponseEntity.ok(weeklyReport);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "get-limit")
    public List<WeeklyReportEntity> getLimit(@RequestBody LimitRequest request){
        return weeklyReportService.getWithLimit(request.getLimit());
    }
    @PostMapping(value = "get-fromto")
    public List<WeeklyReportEntity> getFromDateToDate(@RequestBody FromDateToDateRequest request){
        return weeklyReportService. getFromDateToDate(request.getFrom(), request.getTo());
    }

    @PostMapping(value = "get-noreport-fromto")
    public List<DepartmentDto> getNoReportFromDateToDate(@RequestBody FromDateToDateRequest request){
        return weeklyReportService. getNoReportedFromDateToDate(request.getFrom(), request.getTo());
    }




    @PostMapping(value = "get-by-id")
    public WeeklyReportEntity getById(@RequestBody IdRequest request){
        return  weeklyReportService.getById(request.getId());
    }

    @DeleteMapping(value = "remove")
    public boolean removeById(@RequestBody IdRequest request){
        return weeklyReportService.removeById(request.getId());
    }
}
