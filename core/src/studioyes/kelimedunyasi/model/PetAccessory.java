package studioyes.kelimedunyasi.model;

import com.badlogic.gdx.utils.Array;

public class PetAccessory {

    public enum Category {
        HAT, GLASSES, BOWTIE, CLOTHING, COLOR
    }

    private int id;
    private String name;
    private Category category;
    private int price;
    private String regionName;

    public PetAccessory(int id, String name, Category category, int price, String regionName) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.regionName = regionName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getPrice() { return price; }
    public String getRegionName() { return regionName; }

    // Static list of all available accessories
    private static Array<PetAccessory> allAccessories;

    public static Array<PetAccessory> getAll() {
        if (allAccessories == null) {
            allAccessories = new Array<>();
            // HATS
            allAccessories.add(new PetAccessory(1, "Red Cap", Category.HAT, 100, "acc_hat_red"));
            allAccessories.add(new PetAccessory(2, "Top Hat", Category.HAT, 250, "acc_hat_top"));
            allAccessories.add(new PetAccessory(3, "Propeller Hat", Category.HAT, 500, "acc_hat_propeller"));
            
            // GLASSES
            allAccessories.add(new PetAccessory(10, "Sunglasses", Category.GLASSES, 150, "acc_glasses_sun"));
            allAccessories.add(new PetAccessory(11, "Monocle", Category.GLASSES, 300, "acc_glasses_monocle"));
            
            // BOWTIE
            allAccessories.add(new PetAccessory(20, "Red Bowtie", Category.BOWTIE, 80, "acc_bowtie_red"));
            allAccessories.add(new PetAccessory(21, "Gold Tie", Category.BOWTIE, 400, "acc_tie_gold"));

            // COLORS
            allAccessories.add(new PetAccessory(100, "Default", Category.COLOR, 0, "FFFFFF"));
            allAccessories.add(new PetAccessory(101, "Pink Doodie", Category.COLOR, 200, "FFC0CB"));
            allAccessories.add(new PetAccessory(102, "Blue Doodie", Category.COLOR, 200, "ADD8E6"));
            allAccessories.add(new PetAccessory(103, "Lime Doodie", Category.COLOR, 200, "32CD32"));
            allAccessories.add(new PetAccessory(104, "Golden Doodie", Category.COLOR, 1000, "FFD700"));
        }
        return allAccessories;
    }

    public static PetAccessory getById(int id) {
        for (PetAccessory acc : getAll()) {
            if (acc.getId() == id) return acc;
        }
        return null;
    }
}
