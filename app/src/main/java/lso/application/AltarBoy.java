package lso.application;

public class AltarBoy {
    private String fullname;
    private Integer this_month_points;
    private Integer previous_month_points;
    private Integer all_time_points;

    public AltarBoy() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AltarBoy(String fullname) {
        this.fullname = fullname;
        this.this_month_points = 0;
        this.previous_month_points = 0;
        this.all_time_points = 0;
    }

    public AltarBoy(String fullname, Integer this_month_points, Integer all_time_points) {
        this.fullname = fullname;
        this.this_month_points = this_month_points;
        this.all_time_points = all_time_points;
    }

    public String getFullname(){
        return fullname;
    }

    public Integer getThis_month_points(){
        return this_month_points;
    }

    public void setThis_month_points(Integer this_month_points){
        this.this_month_points = this_month_points;
    }

    public Integer getAll_time_points() {
        return all_time_points;
    }

    public void setAll_time_points(Integer all_time_points) {
        this.all_time_points = all_time_points;
    }

    public Integer getPrevious_month_points() {
        return previous_month_points;
    }

    public void setPrevious_month_points(Integer previous_month_points) {
        this.previous_month_points = previous_month_points;
    }
}
