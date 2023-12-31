package ru.practicum.event.entity;

import lombok.*;
import ru.practicum.category.entity.Category;
import ru.practicum.event.enums.State;
import ru.practicum.request.entity.Request;
import ru.practicum.users.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 2000)
    private String annotation;
    @Column(nullable = false, length = 7000)
    private String description;
    @Column(nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    private Long participantLimit;
    @Column(nullable = false)
    private Boolean requestModeration;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private List<Request> participationRequests;
    @Transient
    private Long confirmedRequests;
    @Transient
    private Long views;
}
