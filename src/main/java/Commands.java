public enum Commands {
    SELECT("select"),
    FROM("from"),
    WHERE("where"),
    OFFSET("offset"),
    LIMIT("limit"),
    AND("and");
    public String code;

    Commands(String code) {
        this.code = code;
    }

    /**
     * Get command from string
     * @param s - string
     * @return Command or null if not found
     */
    public static Commands fromString(String s){
        for(Commands c : Commands.values()){
            if (c.code.equalsIgnoreCase(s))
                return c;
        }
        return null;
    }
    public String getCode() {
        return code;
    }

}
