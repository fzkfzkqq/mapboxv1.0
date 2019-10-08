package com.disastermate.mapbox.Activities;

public class Question {
    int imageId;
    String description;
    Boolean isChecked;

    public Question(int imageId, String description, Boolean question_id) {
        this.imageId = imageId;
        this.description = description;
        this.isChecked
                = question_id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getQuestion_id() {
        return isChecked;
    }

    public void setQuestion_id(Boolean question_id) {
        this.isChecked = question_id;
    }

    @Override
    public String toString() {
        return "Question{" +
                "imageId=" + imageId +
                ", description='" + description + '\'' +
                ", question_id=" + isChecked +
                '}';
    }
}
