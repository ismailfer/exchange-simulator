package com.ismail.exchsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ismail
 * @since 20221001
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError
{
    /**
     * Error code.
     */
    private int errorCode;

    /**
     * Error message.
     */
    private String errorMsg;

    /**
     * Error Detail.
     */
    private String errorDetail;

}
