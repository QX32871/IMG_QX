public class Test {
    public static void main(String[] args) {
        String string = "ahdjakjdhjashkdjhakjhjks-";
        if (string.charAt(string.length() - 1) == '-') {
            System.out.println(string.substring(0, string.length() - 1));
        }
    }
}
