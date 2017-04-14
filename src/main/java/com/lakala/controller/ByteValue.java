package com.lakala.controller;

public class ByteValue {
	
	private byte value;
	
	public ByteValue(){
		
	}
	public ByteValue(byte value){
		this.value = value;
	}
	public ByteValue(int value){
		this.value = (byte)value;
	}
	public int getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
	public void setValue(int value) {
		this.value = (byte)value;
	}
	
	
}
