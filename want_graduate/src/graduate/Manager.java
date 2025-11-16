package graduate;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Manager<T extends Manageable> {
    public ArrayList<T> mList = new ArrayList<>();

    public T find(String name) {
        for (T p : mList) {
            if (p.matches(name))
                return p;
        }
        return null;
    }

    public void readAll(String filename, Factory<T> fac) {
        Scanner filein = null;
        try {
            filein = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        while (filein.hasNext()) {
            T m = fac.create();
            m.read(filein);
            mList.add(m);
            // m.print();
        }
        filein.close();
    }

    public Scanner openFile(String filename) {
        Scanner filein = null;
        try {
            filein = new Scanner(new File(filename));
        } catch (IOException e) {
            System.out.println("파일 입력 오류");
            System.exit(0);
        }
        return filein;
    }

    public void printAll() {
        for (T p : mList)
            p.print();
    }

    public void search(Scanner scan) {
        String kwd = null;
        while (true) {
            System.out.print("검색키워드:");
            kwd = scan.next();
            if (kwd.contentEquals("end"))
                break;
            for (Manageable m : mList) {
                if (m.matches(kwd))
                    m.print();
            }
        }
    }
}
