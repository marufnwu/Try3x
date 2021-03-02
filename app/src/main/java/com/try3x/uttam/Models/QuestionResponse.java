package com.try3x.uttam.Models;

public class QuestionResponse {
    public boolean error;
    public String error_description, ques, quesId;

    public QuestionResponse() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }
}
