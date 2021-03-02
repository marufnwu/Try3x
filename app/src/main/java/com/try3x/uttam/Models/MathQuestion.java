package com.try3x.uttam.Models;

import com.google.gson.annotations.SerializedName;

public class MathQuestion{

	@SerializedName("error_description")
	private String errorDescription;

	@SerializedName("num1")
	private int num1;

	@SerializedName("id")
	private String id;

	@SerializedName("error")
	private boolean error;

	@SerializedName("operator")
	private String operator;

	@SerializedName("num2")
	private int num2;

	public String getErrorDescription(){
		return errorDescription;
	}

	public int getNum1(){
		return num1;
	}

	public String getId(){
		return id;
	}

	public boolean isError(){
		return error;
	}

	public String getOperator(){
		return operator;
	}

	public int getNum2(){
		return num2;
	}
}