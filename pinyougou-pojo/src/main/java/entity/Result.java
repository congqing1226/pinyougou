package entity;

import java.io.Serializable;

/**
 * @author congzi
 * @Description: 返回结果
 * @create 2018-05-04
 * @Version 1.0
 */
public class Result implements Serializable {

    //是否成功
    private boolean success;

    //信息
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
