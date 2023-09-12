package ru.practicum.event.entity;

import lombok.*;
import ru.practicum.category.entity.Category;
import ru.practicum.event.enums.State;
import ru.practicum.location.entity.Location;
import ru.practicum.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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
//    @Column(nullable = false, length = 2000)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    private LocalDateTime createdOn = LocalDateTime.now();
//    @Column(nullable = false, length = 7000)
    private String description;
//    @Column(nullable = false)
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private Boolean paid;
    //    @Column(nullable = false)
    @Column(name = "participant_limit")
    private Long participantLimit;
    private LocalDateTime publishedOn;
    //    @Column(nullable = false)
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private State state;
    private String title;
    @Transient
    private Long confirmedRequests;
    @Transient
    private Long views;
//    @OneToMany
//    @JoinColumn(name = "event_id")
//    private List<Request> participationRequests;
}
