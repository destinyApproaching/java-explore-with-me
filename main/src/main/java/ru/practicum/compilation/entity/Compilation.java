package ru.practicum.compilation.entity;

import lombok.*;
import ru.practicum.event.entity.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean pinned;
    @Column(nullable = false)
    private String title;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "events_compilations",
            joinColumns = @JoinColumn(name = "compilations_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "events_id", referencedColumnName = "id"))
    private List<Event> events;
}
