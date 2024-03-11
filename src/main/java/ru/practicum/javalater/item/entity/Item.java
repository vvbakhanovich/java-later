package ru.practicum.javalater.item.entity;

import lombok.*;
import ru.practicum.javalater.user.entity.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "name")
    private Set<String> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    private String url;

    @Column(name = "resolved_url")
    private String resolvedUrl;

    @Column(name = "mime_type")
    private String mimeType;

    private String title;

    @Column(name = "has_image")
    private Boolean hasImage;

    @Column(name = "has_video")
    private Boolean hasVideo;

    @Column(name = "date_resolved")
    private Instant dateResolved;

    private boolean unread = true;
}
