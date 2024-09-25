import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(new FileReader("test_case.json"));
            BigInteger result = solveSecretSharing(data);
            System.out.println("The constant term 'c' is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BigInteger solveSecretSharing(JSONObject data) {
        JSONObject keys = (JSONObject) data.get("keys");
        int n = ((Long) keys.get("n")).intValue();
        int k = ((Long) keys.get("k")).intValue();

        List<Point> points = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            String key = String.valueOf(i);
            if (data.containsKey(key)) {
                JSONObject point = (JSONObject) data.get(key);
                int x = i;
                BigInteger y = decodeValue((String) point.get("base"), (String) point.get("value"));
                points.add(new Point(x, y));
            }
        }

        // We only need k points to reconstruct the polynomial
        points = points.subList(0, k);

        return lagrangeInterpolation(points, BigInteger.ZERO);
    }

    private static BigInteger decodeValue(String base, String value) {
        return new BigInteger(value, Integer.parseInt(base));
    }

    private static BigInteger lagrangeInterpolation(List<Point> points, BigInteger x) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;  // Start with y_i
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    numerator = numerator.multiply(x.subtract(BigInteger.valueOf(points.get(j).x)));
                    denominator = denominator.multiply(BigInteger.valueOf(points.get(i).x - points.get(j).x));
                }
            }

            term = term.multiply(numerator).divide(denominator);  // Multiply the term with (L_i(x))
            result = result.add(term);
        }

        return result;
    }

    private static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
