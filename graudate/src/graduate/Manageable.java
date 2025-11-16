package graduate;

import java.util.Scanner;

public interface Manageable {

    void read(Scanner scan);
    boolean matches(String kwd);
    boolean matches(String[] kwds);
}
