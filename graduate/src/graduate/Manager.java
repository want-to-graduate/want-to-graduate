package graduate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class Manager<T extends Manageable> {
    public ArrayList<T> mList = new ArrayList<>();

    public T find(String name) {
        for (T p : mList) {
            if (p.matches(name))
                return p;
        }
        return null;
    }

    public T find(String[] kwds) {
        for (T p : mList) {
            if (p.matches(kwds))
                return p;
        }
        return null;
    }
    
    public List<T> filterBy(Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T m : mList) {
            if (condition.test(m))
                result.add(m);
        }
        return result;
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
        }
        filein.close();
    }

    // 학생 수강 내역 파일에서 과목 id를 읽은 후 List로 반환
    public List<Integer> readFile(String filename) {
    	List<Integer> ids = new ArrayList<>();
    	Scanner filein = null;
    	try {
            filein = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
        	return ids;
        }
    	while (filein.hasNext()) {
    		String token = filein.next().trim();
    		if (token.equals("-")) break;
    		
    		try {
    			ids.add(Integer.parseInt(token));
    		} catch (NumberFormatException e) {
    			
    		}
    	}
    	filein.close();
    	return ids;
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


    public List<T> search(String kwd) {
        List<T> list = new ArrayList<>();
        for (T m : mList) {
            if (m.matches(kwd)) {
                list.add(m);
            }
        }
        return list;
    }
}
