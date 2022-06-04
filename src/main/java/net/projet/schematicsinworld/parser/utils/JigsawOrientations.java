package net.projet.schematicsinworld.parser.utils;

public enum JigsawOrientations {
    EAST ("east_up"),
    NORTH ("north_up"),
    SOUTH ("south_up"),
    WEST ("west_up");
    //    DOWN_EAST ("down_east"),
    //    DOWN_NORTH ("down_north"),
    //    DOWN_SOUTH ("down_south"),
    //    DOWN_WEST ("down_west"),
    //    UP_EAST ("up_east"),
    //    UP_NORTH ("up_north"),
    //    UP_SOUTH ("up_south"),
    //    UP_WEST ("up_west");

    private String id;

    JigsawOrientations(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
