package com.pokedex.pokedex_api.entities;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="response")
public class ApiResponse<T> {
    private boolean isSuccess;
    private String message;
    private T result;

    public ApiResponse() {
    }

    public ApiResponse(boolean isSuccess, String message, T result) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.result = result;
    }
    @XmlElement(name = "isSuccess")
    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }
    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

