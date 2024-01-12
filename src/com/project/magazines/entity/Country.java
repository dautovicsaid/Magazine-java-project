package com.project.magazines.entity;


public class Country {
    private Long id;
    private String name;

    public Country(String name) {
        this.id = null;
        this.name = name;
    }

    public Country(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return """
                {
                    "id": %d,
                    "name": "%s"
                }""".formatted(id, name);
    }
}


