package service.data;

import real.player.Player;

public class MathGen {
    public static String generateAddition(Player pl) {
        int num1 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num1++;
        int num2 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num2++;
        if (num1 > num2) {
            pl.answer = num1;
            return num1 - num2 + " + " + num2 + " = ";
        } else {
            pl.answer = num2;
            return num2 - num1 + " + " + num1 + " = ";
        }
    }

    /*This method is used to generate a new subtraction problem. The method generates two int variables with random values. It will determine which value
      is greater, and set it as the answer to the equation. The returned equation is (bigger number + smaller number) - (smaller number) = (bigger number).*/
    public static String generateSubtraction(Player pl) {
        int num1 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num1++;
        int num2 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num2++;
        if (num2 > num1) {
            pl.answer = num2;
            return (num2 + num1) + " - " + num1 + " = ";
        } else {
            pl.answer = num1;
            return (num1 + num2) + " - " + num2 + " = ";
        }
    }

    /*This method is used to generate a new multiplication problem. The method creates two random int variables, one with a value from 0-100, and the second
      with a value from 0-10. The first variable is the answer to the equation. The second variable is used to divide the first variable to find the other
      factor in the equation. The returned equation is (bigger number / smaller number) * (smaller number) = (bigger number).*/
    public static String generateMultiplication(Player pl) {
        int num1 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num1++;
        int num2 = ((int)(Math.random() * 10));
        if (Math.random() < 0.50)
            num2++;
        if (num1 % num2 != 0)
            num1 -= num1 % num2;
        pl.answer = num1;
        return pl.answer / num2 + " \u00D7 " + num2 + " = ";
    }

    /*This method is used to generate a new division problem. The method creates two random int variables with values from 0-100. The first int generated is used
      as the answer to the equation. The dividend of the equation is found by multiplying the two random ints together. The returned equation is 
      (number1 * number2) / (number2) = (number1).*/
    public static String generateDivision(Player pl) {
        int num1 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num1++;
        int num2 = ((int)(Math.random() * 10)) + (((int)(Math.random() * 10)) * 10);
        if (Math.random() < 0.50)
            num2++;
        pl.answer = num1;
        return (num1 * num2) + " \u00F7 " + num2 + " = ";
    }

    //This method will choose a random math problem to generate each time it is called. 
    public static String generateRandom(Player pl) {
        double x = Math.random();
        if (x < 0.25) {
            return generateAddition(pl);
        } else if (x < 0.5) {
            return generateSubtraction(pl);
        } else if (x < 0.75) {
            return generateMultiplication(pl);
        } else {
            return generateDivision(pl);
        }
    }
}