package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdOrderByTimestamp(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorIdOrderByTimestamp(Long author, Pageable pageable);
}
