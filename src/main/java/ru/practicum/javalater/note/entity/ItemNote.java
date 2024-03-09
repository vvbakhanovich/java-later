package ru.practicum.javalater.note.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.javalater.item.entity.Item;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "item_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 2000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "date_of_note")
    private Instant dateOfNote;
}
