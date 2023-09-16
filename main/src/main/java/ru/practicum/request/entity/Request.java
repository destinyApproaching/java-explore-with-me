package ru.practicum.request.entity;

import lombok.*;
import ru.practicum.event.entity.Event;
import ru.practicum.request.enums.Status;
import ru.practicum.users.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
