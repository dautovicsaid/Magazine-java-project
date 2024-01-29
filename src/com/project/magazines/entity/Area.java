package com.project.magazines.entity;

public class Area {
    private Long id;
    private String name;

    public Area(String name) {
        this.name = name;
    }

    public Area(Long id, String name) {
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
        return String.format("{" +
                "\n  \"id\": %d," +
                "\n  \"name\": \"%s\"" +
                "\n}", id, name);
    }
}
