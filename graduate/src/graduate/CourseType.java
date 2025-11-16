package graduate;

public enum CourseType {
    MAJOR_REQUIRED("전필"),
    MAJOR_ELECTIVE("전선"),
    FACULTY_REQUIRED("학필"),
    FACULTY_ELECTIVE("학선"),
    NONE("없음");

    private final String description;

    CourseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}