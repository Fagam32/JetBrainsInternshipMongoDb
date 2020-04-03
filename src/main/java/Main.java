public class Main {
    public static void main(String[] args) {
        Parser parserV2 = new Parser();
        System.out.println(parserV2.parse("SELECT name FROM database WHERE d =5 and e = 'VasyA'"));
        System.out.println(parserV2.parse("SELECT name, surname FROM collection"));
    }
}
