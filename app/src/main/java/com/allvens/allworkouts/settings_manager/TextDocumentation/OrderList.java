package com.allvens.allworkouts.settings_manager.TextDocumentation;

public class OrderList {
    private int orderTracker = 0;
    private char[] alphabet  = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public String get_NextAlpa(){
        orderTracker++;

        if(orderTracker > 26){
            orderTracker = 1;
        }

        return "( " + Character.toString(alphabet[orderTracker - 1]) + " ) ";
    }

    public String getNextPos(){
        orderTracker++;

        return ((orderTracker) + ". ");
    }
}
