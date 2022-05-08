package com.example.backend.models;

import javax.persistence.*;

@Entity
@Table(name = "paintings")
@Access(AccessType.FIELD)
public class Painting {
    public Painting() { }
    public Painting(Long id) {
        this.id = id;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @ManyToOne
    @JoinColumn(name = "artistid")
    public Artist artistid;

    @ManyToOne
    @JoinColumn(name = "museumid")
    public Museum museumid;

    @Column(name = "year")
    public long year;
}
