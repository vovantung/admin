package txu.admin.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "DEPARTMENT")
public class DepartmentEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Getter
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    @Getter
    private String firstName;

    @Column(name = "CREATED_AT")
    private Date createdAt;
    public String getCreatedAt() {
        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
    }

    @Column(name = "UPDATED_AT")
    private Date updateAt;
    public String getUpdateAt() {
        return updateAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
    }

}
