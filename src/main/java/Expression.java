public class Expression {
    private String variable;
    private String operation;
    private String value;

    public Expression(String variable, String operation, String value) {
        this.variable = variable;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(variable).append(": ");
        switch (operation) {
            case "=":
                return sb.append(value).toString();
            case "<>":
                return sb.append("{$ne: ").append(value).append("}").toString();
            case "<":
                return sb.append("{$lt: ").append(value).append("}").toString();
            default:
                return sb.append("{$gt: ").append(value).append("}").toString();
        }
    }
}
