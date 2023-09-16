package ru.practicum.event.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Float lat;
    private Float lon;
}
