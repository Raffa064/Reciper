package com.raffa064.reciper.screens;

public class Test {
    public static void main(String[] args) {
		String text = "A: B, C \n A:B,C \n A: B         ,  345C45";
		for (String line : text.split("\n")) {
			line = line.trim();
			System.out.println("'"+line+"' "+line.matches("[a-zA-Z0-9\\-_]{1,}\\s*:\\s*[a-zA-Z0-9\\-_]{1,}\\s*,\\s*[a-zA-Z0-9\\-_]{1,}"));
		}
	} 
}
