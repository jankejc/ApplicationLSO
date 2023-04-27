package lso.application;

public class Services {
    private String name_of_service;
    private Integer how_many_points;

    public Services() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Services(String name_of_service, Integer how_many_points) {
        this.name_of_service = name_of_service;
        this.how_many_points = how_many_points;
    }

    public String getName_of_service(){
        return name_of_service;
    }

    public Integer getHow_many_points(){
        return how_many_points;
    }
}
