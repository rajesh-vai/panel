package search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Rajesh on 08-10-2016.
 */
public class ConvertJabong {
    static String fields[] = {"Rise", "Sole Material", "Closing", "Tip shape", "Upper Material Details", "Inner Lining", "Heel shape", "Heel height", "Shoe Care", "Ankle Height", "Shoe Weight", "Technology", "Age Group", "Rim Type", "Device Brand", "Device Model", "Care label", "Number of Card Slots", "No. of Mobile Pouches", "Package Content", "Base Unit of Measure", "Attribute", "Formulation", "How to Use", "Pockets", "Jeans Wash Effect", "Lowers - Fly", "Product Weight without Package", "Stone Type", "Upper Material", "Type", "Brand Fit Name", "Work Type", "Plating", "Spikes", "Additional Information", "Finish-Coating", "Assembly Type", "Assembly Detail", "Kit Contents & How to Use", "Ingrediants", "Tips & Tricks", "Lens Type", "Frame Shape", "Frame Color", "Frame material", "Lens Color", "Lens Material", "UV Protected", "Product Dimensions", "Product warranty", "Movement", "Dial Shape", "Dial Color", "Dial Diameter", "Dial Thickness (mm)", "Strap Color", "Strap Material", "Model Number", "Model Stats", "Additional Strap", "Water Resistance", "USP", "Type of Closure", "No. of Zips", "Special features", "Capacity(in Litres)", "Extra pair Lens", "Extra Handle", "Number of Compartments", "No. of Pockets", "Laptop Compartment", "Laptop Size", "Water Resistant", "Polarized", "Fabric", "Sleeves", "Fit", "Length", "Neck", "Fabric Details", "Fabric Material", "Secondary Color", "Material", "Style", "Size", "Quality", "Color", "Package Contents", "Key Features", "inStock"};

    public static void main(String[] args) {
        String fileName = "D:\\Rajesh\\SR\\Jabong\\Jabong_jsonsFeed\\output.json";

        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> {
                processJsons(line);

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processJsons(String json) {
        try {

            Gson gson = new GsonBuilder().create();

            JsonObject job = gson.fromJson(json, JsonObject.class);
            JsonElement product = job.getAsJsonObject("product");
            String url = product.getAsJsonObject().getAsJsonObject("url").get("url").getAsString();
            JsonElement base = product.getAsJsonObject().getAsJsonObject("base");

            String title = base.getAsJsonObject().get("title").getAsString();
            String brand = base.getAsJsonObject().get("brand").getAsString();
            String model = base.getAsJsonObject().get("model").getAsString();
            String category = base.getAsJsonObject().get("category").getAsString();
            String categoryPath = base.getAsJsonObject().get("categoryPath").getAsString();
            String currency = base.getAsJsonObject().get("currency").getAsString();
            double price = base.getAsJsonObject().get("sale price").getAsDouble();
            price = price ==  0 ? Math.random() * (1999 + 399) : price;
            Map myMap = new HashMap();
            myMap.put("url", product.getAsJsonObject().getAsJsonObject("url").get("url").getAsString());
            myMap.put("pid", product.getAsJsonObject().getAsJsonObject("pid").get("pid").getAsString());
            myMap.put("title", base.getAsJsonObject().get("title").getAsString());
            myMap.put("brand", base.getAsJsonObject().get("brand").getAsString());
            myMap.put("model", base.getAsJsonObject().get("model").getAsString());
            myMap.put("category", base.getAsJsonObject().get("category").getAsString());
            myMap.put("categoryPath", base.getAsJsonObject().get("categoryPath").getAsString());
            myMap.put("currency", base.getAsJsonObject().get("currency").getAsString());
            myMap.put("price", (int) price);
            myMap.put("currency",  base.getAsJsonObject().get("retail price").getAsDouble());

            String dim[] = {};
            if (product.getAsJsonObject().has("Product Dimensions (Length x Breadth)")) {
                String str = product.getAsJsonObject().get("Product Dimensions (Length x Breadth)").getAsJsonObject().get("values").getAsString().replaceAll("[^-?0-9]+", " ");
                dim = str.trim().split(" ");
                myMap.put("dimension_length", Integer.parseInt(dim[0]));
                myMap.put("dimension_breadth", Integer.parseInt(dim[1]));
                myMap.put("dimension_height", 0);
            }
            System.out.println(dim[0]);
            JsonElement specs;
            List<String> specsArray = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                if (product.getAsJsonObject().has(fields[i])) {
                    specs = product.getAsJsonObject().getAsJsonObject(fields[i]);
                    if (specs.getAsJsonObject().get("type").getAsString().equals("boolean")) {
                        specsArray.add(fields[i]);
                    } else {
                        specsArray.add(fields[i] + " - " + specs.getAsJsonObject().get("values").getAsString());
                    }
                }
            }

            myMap.put("specs", specsArray);
            String output = gson.toJson(myMap);
            output = output + "\n";
            System.out.println(output);
            Files.write(Paths.get("D:\\Rajesh\\SR\\Jabong\\gen.json"), output.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
