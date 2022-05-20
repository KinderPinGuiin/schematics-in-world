package net.projet.schematicsinworld.parser.utils;

import net.projet.schematicsinworld.parser.tags.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockData {

    /*
     * Attributs
     */

    private int x;
    private int y;
    private int z;
    private int state;
    private HashMap<String, Tag> nbt;

    /*
     * Constructeur
     */

    public BlockData(int x, int y, int z, int state, @NotNull HashMap<String, Tag> nbt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.state = state;
        this.nbt = nbt;
    }

    /*
     * Requêtes
     */

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public ArrayList<Integer> getCoords() {
        ArrayList<Integer> coords = new ArrayList<>();
        coords.add(this.getX());
        coords.add(this.getY());
        coords.add(this.getZ());

        return coords;
    }

    public int getState() {
        return this.state;
    }

    public HashMap<String, Tag> getNbt() {
        return new HashMap<>(this.nbt);
    }

    @Override
    public String toString() {
        return "BlockData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", state=" + state +
                ", nbt=" + nbt +
                '}';
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setNbt(HashMap<String,Tag> map) {
        this.nbt = map;
    }
}
