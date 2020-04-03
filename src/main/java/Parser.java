import java.util.ArrayList;
import java.util.List;

public class Parser {
    private String query;
    private String[] splitQuery;
    private int currentPosition;

    public String parse(String query) {
        this.query = query;
        return start();
    }

    private String start() {
        splitQuery = query.split("[\\s|,]+"); // "words" from string
        currentPosition = 0;
        List<String> selectList = new ArrayList<>();
        List<Expression> expressionList = new ArrayList<>();
        String databaseName = null;
        int offset = -1;
        int limit = -1;

        for (; currentPosition < splitQuery.length; ) {
            switch (getCommand(splitQuery[currentPosition])) {
                case SELECT:
                    currentPosition++;
                    selectList = getSelectVariables();
                    currentPosition += selectList.size();
                    break;
                case FROM:
                    currentPosition++;
                    databaseName = getDatabaseName();
                    currentPosition++;
                    break;
                case WHERE:
                    currentPosition++;
                    expressionList = getExpressionList();
                    break;
                case OFFSET:
                    currentPosition++;
                    offset = getOffset();
                    currentPosition++;
                    break;
                case LIMIT:
                    currentPosition++;
                    limit = getLimit();
                    currentPosition++;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return createParsedString(databaseName, selectList, expressionList, offset, limit);
    }

    private int getLimit() {
        return Integer.parseInt(splitQuery[currentPosition]);
    }

    private int getOffset() {
        return Integer.parseInt(splitQuery[currentPosition]);
    }

    private List<Expression> getExpressionList() {
        List<Expression> list = new ArrayList<>();
        list.add(getSingleExpression());
        while (currentPosition != splitQuery.length && getCommand(splitQuery[currentPosition]) == Commands.AND) {
            currentPosition++;
            list.add(getSingleExpression());
        }
        return list;
    }

    private Expression getSingleExpression() {
        for (; ; ) {
            String variable, operation, value;
            String currentPiece = splitQuery[currentPosition];
            if (currentPiece.contains(">") || currentPiece.contains("<") || currentPiece.contains("=")) { //contains operation
                int operationIndex;
                if (currentPiece.contains("=")) {
                    operation = "=";
                    operationIndex = currentPiece.indexOf('=');
                } else if (currentPiece.contains("<") && currentPiece.contains(">")) {
                    operation = "<>";
                    operationIndex = currentPiece.indexOf('<');
                } else if (currentPiece.contains("<")) {
                    operation = "<";
                    operationIndex = currentPiece.indexOf("<");
                } else {
                    operation = ">";
                    operationIndex = currentPiece.indexOf(">");
                }
                boolean outOfLeftCheck = !(operationIndex - 1 >= 0);
                boolean outOfRightCheck = !(operationIndex + operation.length() < currentPiece.length());
                if (!outOfLeftCheck && !outOfRightCheck) {          // if all together
                    String[] split = currentPiece.split(operation);
                    variable = split[0];
                    value = split[1];
                    currentPosition++;
                    return new Expression(variable, operation, value);
                } else if (!outOfLeftCheck) {                       // if there's something on the left and nothing on the right
                    variable = currentPiece.split(operation)[0];
                    value = splitQuery[currentPosition + 1];
                    currentPosition += 2;
                    return new Expression(variable, operation, value);
                } else if (!outOfRightCheck) {                      // if there's something on the right and nothing on the left
                    variable = splitQuery[currentPosition - 1];
                    value = currentPiece.split(operation)[1];
                    currentPosition++;
                    return new Expression(variable, operation, value);
                } else {                                            // if everything is split
                    variable = splitQuery[currentPosition - 1];
                    value = splitQuery[currentPosition + 1];
                    currentPosition += 2;
                    return new Expression(variable, operation, value);
                }
            } else
                currentPosition++;
        }
    }

    private String getDatabaseName() {
        return splitQuery[currentPosition];
    }

    private List<String> getSelectVariables() {
        List<String> list = new ArrayList<>();
        for (int i = currentPosition; i < splitQuery.length; i++) {
            if (getCommand(splitQuery[i]) == null)
                list.add(splitQuery[i]);
            else break;
        }
        return list;
    }

    private Commands getCommand(String command) {
        command = command.toLowerCase();
        return Commands.fromString(command);
    }

    private String createParsedString(String database,
                                      List<String> selectList,
                                      List<Expression> expressionList,
                                      int offset,
                                      int limit) {

        StringBuilder sb = new StringBuilder();
        boolean selectAll = selectList.get(0).equals("*");
        sb.append("db.").append(database).append(".find(");

        if (selectAll && expressionList.isEmpty())
            sb.append("{}");
        else if (selectAll) {
            sb.append("{");
            for (Expression exp : expressionList) {
                sb.append(exp.toString()).append(", ");
            }
            sb.replace(sb.length() - 2, sb.length(), "}");
        } else if (expressionList.isEmpty()) {
            sb.append("{}, {");
            for (String s : selectList)
                sb.append(s).append(": 1, ");
            sb.replace(sb.length() - 2, sb.length(), "}");
        } else {
            sb.append("{");
            for (Expression exp : expressionList) {
                sb.append(exp.toString()).append(", ");
            }
            sb.replace(sb.length() - 2, sb.length(), "},{");
            for (String s : selectList)
                sb.append(s).append(": 1, ");
            sb.replace(sb.length() - 2, sb.length(), "}");
        }
        sb.append(")");

        if (offset != -1)
            sb.append(".skip(").append(offset).append(")");
        if (limit != -1)
            sb.append(".limit(").append(limit).append(")");

        return sb.toString();
    }
}
