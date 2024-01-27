import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Program {
    // This function returns the union of two lists.
    public static ArrayList<String> union(ArrayList<String> list1, ArrayList<String> list2) {
        Set<String> set = new HashSet<String>();
        set.addAll(list1);
        set.addAll(list2);
        return new ArrayList<String>(set);
    }

    // This function returns the key whose value is given on the map.
    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // This function counts the occurrence of a character in a string.
    public static int CountOccuranceOfChar(String str, String letter) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == letter.charAt(0)) {
                count++;
            }
        }
        return count;
    }

    // This function generates a list of boolean arrays containing all the different boolean arrays of the given length.
    public static ArrayList<boolean[]> combine(int length) {
        ArrayList<boolean[]> list = new ArrayList<boolean[]>();
        for (int i = 0; i < Math.pow(2, length); i++) {
            String bin = Integer.toBinaryString(i);
            while (bin.length() < length)
                bin = "0" + bin;
            char[] chars = bin.toCharArray();
            boolean[] boolArray = new boolean[length];
            for (int j = 0; j < chars.length; j++) {
                boolArray[j] = chars[j] == '0' ? true : false;
            }
            list.add(boolArray);
        }
        return list;
    }

    // This function generates new states according to the probability that the given state is an epsilon or not according to the given letter.
    public static ArrayList<String> combinations(String state, String letter) {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<boolean[]> list2 = combine(CountOccuranceOfChar(state, letter));
        for (boolean[] arr : list2) {
            String tempState = state;
            for (boolean flag : arr) {
                if (flag) {
                    tempState = tempState.replaceFirst(letter, ".");
                } else {
                    tempState = tempState.replaceFirst(letter, "€");
                }
            }
            tempState = tempState.replace(".", letter);
            tempState = tempState.replace("€", "");
            if (tempState.length() != 0) {
                list.add(tempState);
            } else {
                list.add("€");
            }
        }
        return list;
    }

    // This function extends epsilon productions in the given map of productions.
    public static void extendEpsilon(Map<String, ArrayList<String>> productions) {
        ArrayList<String> nullableVariables = new ArrayList<String>();
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                if (cases.equals("€")) {
                    nullableVariables.add(entry.getKey());
                }
            }
        }
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            ArrayList<String> list = new ArrayList<String>();
            for (String cases : entry.getValue()) {
                for (String nullableVariable : nullableVariables) {
                    if (cases.contains(nullableVariable)) {
                        list = union(list, combinations(cases, nullableVariable));
                    }
                }
            }
            entry.setValue(union(list, entry.getValue()));
        }
    }

    // This function eliminates epsilon productions from the given map of productions.
    public static void eliminateEpsilon(Map<String, ArrayList<String>> productions) {
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                if (!entry.getKey().equals("S") && cases.equals("€")) {
                    entry.getValue().remove("€");
                    break;
                }
            }
        }
    }

    // This function extends unit productions in the given map of productions.
    public static void extendUnitProductions(Map<String, ArrayList<String>> productions, String[] alphabet) {
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                boolean isTerminal = false;
                if (cases.length() == 1) {
                    for (String letter : alphabet) {
                        if (cases.equals(letter) || cases.equals("€")) {
                            isTerminal = true;
                            break;
                        }
                    }
                    if (!isTerminal && productions.get(cases) != null) {
                        ArrayList<String> unionList = union(productions.get(cases), entry.getValue());
                        productions.replace(entry.getKey(), unionList);
                    }
                }
            }
        }
    }

    // This function eliminates unit productions from the given map of productions.
    public static void eliminateUnitProductions(Map<String, ArrayList<String>> productions, String[] alphabet) {
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                boolean isTerminal = false;
                if (cases.length() == 1) {
                    for (String letter : alphabet) {
                        if (cases.equals(letter) || cases.equals("€")) {
                            isTerminal = true;
                            break;
                        }
                    }
                    if (!isTerminal) {
                        ArrayList<String> tempList = new ArrayList<>(productions.get(entry.getKey()));
                        tempList.remove(cases);
                        productions.replace(entry.getKey(), tempList);
                    }
                }
            }
        }
    }

    // This function eliminates terminal symbols from the given map of productions.
    public static void eliminateTerminals(Map<String, ArrayList<String>> productions, String[] alphabet) {
        int startPoint = 65;
        int i = 0;
        ArrayList<String> tempList;
        while (i != alphabet.length) {
            if (!productions.containsKey(Character.toString((char) startPoint))) {
                tempList = new ArrayList<>();
                tempList.add(alphabet[i++]);
                productions.put(Character.toString((char) startPoint), tempList);
            } else
                startPoint++;
        }
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                if (cases.length() > 1) {
                    for (String letter : alphabet) {
                        while (cases.contains(letter)) {
                            tempList = new ArrayList<>();
                            tempList.add(letter);
                            String key = getKey(productions, tempList);
                            int index = cases.indexOf(letter);
                            int index2 = entry.getValue().indexOf(cases);
                            cases = cases.substring(0, index) + key + cases.substring(index + 1);
                            entry.getValue().set(index2, cases);
                        }
                    }
                }
            }
        }
    }

    // This function breaks variable strings longer than 2 in the given map of productions.
    public static Map<String, ArrayList<String>> breakVariables(Map<String, ArrayList<String>> productions) {
        int startPoint = 65;
        ArrayList<String> tempList;
        Map<String, ArrayList<String>> tempProduct = new LinkedHashMap<>(productions);
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                while (cases.length() > 2) {
                    tempList = new ArrayList<>();
                    tempList.add(cases.substring(0, 2));
                    while (true) {
                        if (tempProduct.containsValue(tempList)) {
                            break;
                        } else if (!tempProduct.containsKey(Character.toString((char) startPoint))) {
                            tempProduct.put(Character.toString((char) startPoint), tempList);
                            break;
                        } else
                            startPoint++;
                    }
                    String key = getKey(tempProduct, tempList);
                    cases = key + cases.substring(2);
                }
            }
        }
        return tempProduct;
    }

    // This function transforms the given map of productions into Chomsky Normal Form (CNF).
    public static void CNF(Map<String, ArrayList<String>> productions) {
        ArrayList<String> tempList;
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            for (String cases : entry.getValue()) {
                while (cases.length() > 2) {
                    tempList = new ArrayList<>();
                    tempList.add(cases.substring(0, 2));
                    String key = getKey(productions, tempList);
                    int index = productions.get(entry.getKey()).indexOf(cases);
                    cases = key + cases.substring(2);
                    productions.get(entry.getKey()).set(index, cases);
                }
            }
        }
    }

    // This function prints the given map of productions.
    public static void printMap(Map<String, ArrayList<String>> productions) {
        for (Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            System.out.print(entry.getKey() + "-");
            for (int i = 0; i < entry.getValue().size(); i++) {
                System.out.print(entry.getValue().get(i));
                if (i != entry.getValue().size() - 1) {
                    System.out.print("|");
                } else
                    System.out.println();
            }
        }
    }

    // The main function reads the productions from a file, processes them according to the CNF conversion algorithm, and prints the resulting productions.
    public static void main(String[] args) throws IOException {
        // create a map of productions
        Map<String, ArrayList<String>> productions = new LinkedHashMap<>();
        String alphabet[];
        File file = new File("CFG.txt");
        try (Scanner scan = new Scanner(file, "UTF-8")) {
            String line = scan.nextLine();
            alphabet = (line.split("=")[1]).split(",");
            while (scan.hasNextLine()) {
                ArrayList<String> string = new ArrayList<>();
                line = scan.nextLine();
                String array[] = line.substring(2).split("\\|");
                for (String value : array) {
                    string.add(value);
                }

                productions.put(line.substring(0, 1), string);
            }
        }
        System.out.println("CFG Form");
        printMap(productions);
        for (int i = 0; i < productions.size() - 1; i++) {//this loop is to avoid errors as a result of nested or looped variables.
            extendEpsilon(productions);
        }
        eliminateEpsilon(productions);
        System.out.println("\nEliminate €");
        printMap(productions);
        for (int i = 0; i < productions.size() - 1; i++) {//this loop is to avoid errors as a result of nested or looped variables.
            extendUnitProductions(productions, alphabet);
        }
        eliminateUnitProductions(productions, alphabet);
        System.out.println("\nEliminate unit production");
        printMap(productions);
        eliminateTerminals(productions, alphabet);
        System.out.println("\nEliminate terminals");
        printMap(productions);
        productions = breakVariables(productions);
        System.out.println("\nBreak variable strings longer than 2");
        printMap(productions);
        CNF(productions);
        System.out.println("\nCNF");
        printMap(productions);
    }
}
