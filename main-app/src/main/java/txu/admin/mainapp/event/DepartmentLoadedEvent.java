package txu.admin.mainapp.event;

import txu.admin.mainapp.entity.DepartmentEntity;

public record DepartmentLoadedEvent(int id, DepartmentEntity dept) {}