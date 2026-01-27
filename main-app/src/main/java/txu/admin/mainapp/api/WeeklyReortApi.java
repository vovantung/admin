package txu.admin.mainapp.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import txu.admin.mainapp.base.AbstractApi;
import txu.admin.mainapp.dto.*;
import txu.admin.mainapp.entity.WeeklyReportEntity;
import txu.admin.mainapp.service.WeeklyReportService;

import java.util.List;

@RestController
@RequestMapping("/admin/weekly-report")
@RequiredArgsConstructor
public class WeeklyReortApi extends AbstractApi {


    private final WeeklyReportService weeklyReportService;

    @PostMapping("/get-presignedurl-for-get")
    public LinkDto getPreSignedUrlForGet(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            String pre_signed_url =  weeklyReportService.getPreSignedUrlForGet(request.getFilename());
            linkDto.setPre_signed_url(pre_signed_url);
        } catch (Exception e) {

        }
        return linkDto;
    }

    @PostMapping("/get-presignedurl-for-put")
    public LinkDto getPreSignedUrlForPut(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            return weeklyReportService.getPreSignedUrlForPut(request.getFilename());
        } catch (Exception e) {

        }
        return linkDto;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReport(@RequestBody UploadfileInfoRequest request) {
        try {
            WeeklyReportEntity weeklyReport = weeklyReportService.addReport(request);
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
