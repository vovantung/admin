package txu.admin.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.admin.mainapp.base.AbstractApi;
import txu.admin.mainapp.dto.IdRequest;
import txu.admin.mainapp.dto.LimitRequest;
import txu.admin.mainapp.entity.DepartmentEntity;
import txu.admin.mainapp.service.DepartmentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentApi extends AbstractApi {

    private final DepartmentService departmentService;

    @PostMapping(value = "create-or-update")
    public DepartmentEntity createOrUpdate(@RequestBody DepartmentEntity department){
        return departmentService.createOrUpdate(department);
    }

    @PostMapping(value = "get-limit")
    public List<DepartmentEntity> getLimit(@RequestBody LimitRequest request){
        return departmentService.getWithLimit(request.getLimit());
    }

    @PostMapping(value = "get-by-id")
    public DepartmentEntity getById(@RequestBody IdRequest request){
        return  departmentService.getById(request.getId());
    }

    @DeleteMapping(value = "remove")
    public boolean removeById(@RequestBody IdRequest request){
        return departmentService.removeById(request.getId());
    }

}
