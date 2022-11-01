package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String fileName = "data.csv";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data1.json");


        String fileName1 = "data.xml";
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");


    }

    public static List<Employee> parseXML(String fileName1) {
        List<Employee> list = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName1));
            Node root = doc.getDocumentElement();
            System.out.println("Корневой элемент: " + root.getNodeName());
            list = read(root);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static List<Employee> read(Node node) {
        List<Employee> list = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() && node_.getNodeName().equals("employee")) {
                Element element = (Element) node_;

                String[] s = element.getTextContent().split("\n");

                Long id = Long.parseLong(s[1].trim());

                String firstName = s[2].trim();
                String lastName = s[3].trim();
                String country = s[4].trim();
                int age = Integer.parseInt(s[5].trim());

                Employee employee = new Employee(id,firstName,lastName, country, age);

                list.add(employee);
                read(node_);
            }
        }
        return list;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter file = new
                FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list);
    }


    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
           list = csv.parse();
            list.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }
}