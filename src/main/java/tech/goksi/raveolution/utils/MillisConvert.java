package tech.goksi.raveolution.utils;

public class MillisConvert {
    public static long toMilli(String s){
        long result = 0;
        String number = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                number += c;
            } else if (Character.isLetter(c) && !number.isEmpty()) {
                result += convert(Integer.parseInt(number), c);
                number = "";
            }else throw new NumberFormatException();
        }
        if(result == 0) throw new NumberFormatException();
        return result;
    }
    private static long convert(int value, char unit) {
        switch(unit) {
            case 'd' : return (long) value * 1000*60*60*24;
            case 'h' : return (long) value * 1000*60*60;
            case 'm' : return (long) value * 1000*60;
            case 's' : return value * 1000L;
        }
        return 0;
    }
}
