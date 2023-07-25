package com.itso.occupancy.occupancy.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "feature",
        indexes = {
                @Index(columnList = "code",name = "ix_code"),
                @Index(columnList = "name",name = "ix_name")
        })
public class Feature extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false)
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL)
    private List<WorkTask> workTasks;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_project")
    private Project project;

    @NotNull
    @Column(nullable = false, columnDefinition = "boolean default false") // Default value
    private Boolean supprimer = false;

    @Column(nullable = false)
    private double Temps_E;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date Date_E;
}
