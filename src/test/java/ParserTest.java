import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    Parser parser;

    @Before
    public void beforeTest() {
        parser = new Parser();
    }

    @Test
    public void baseLineTest() {
        String base = "SELECT * FROM sales LIMIT 10";
        String result = "db.sales.find({}).limit(10)";
        assertEquals(result, parser.parse(base));

        base = "SELECT name, surname FROM collection";
        result = "db.collection.find({}, {name: 1, surname: 1})";
        assertEquals(result, parser.parse(base));

        base = "SELECT * FROM collection OFFSET 5 LIMIT 10";
        result = "db.collection.find({}).skip(5).limit(10)";
        assertEquals(result, parser.parse(base));

        base = "SELECT * FROM customers WHERE age > 22 AND name = 'Vasya'";
        result = "db.customers.find({age: {$gt: 22}, name: 'Vasya'})";
        assertEquals(result, parser.parse(base));
    }

    @Test
    public void completeTest() {
        String base = "SELECT name FROM database WHERE d =5 and e = 'VasyA'";
        String result = "db.database.find({d: 5, e: 'VasyA'},{name: 1})";
        assertEquals(result, parser.parse(base));


        base = "SELECT name, surname FROM database WHERE a < 5 AnD b> 4 and c<>3 and d =5 and e = 'VasyA' offset 5 limit 10";
        result = "db.database.find({a: {$lt: 5}, b: {$gt: 4}, c: {$ne: 3}, d: 5, e: 'VasyA'},{name: 1, surname: 1}).skip(5).limit(10)";
        assertEquals(result, parser.parse(base));

        base = "Select * from data limit 5 offset 5";
        result = "db.data.find({}).skip(5).limit(5)";
        assertEquals(result, parser.parse(base));

        base = "Select qwerty from ytrewq where zxc <> 123 ";
        result = "db.ytrewq.find({zxc: {$ne: 123}},{qwerty: 1})";
        assertEquals(result, parser.parse(base));
    }
}