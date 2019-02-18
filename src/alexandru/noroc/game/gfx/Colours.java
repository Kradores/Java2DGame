package alexandru.noroc.game.gfx;

public class Colours
{
    public static int get(int colour_1, int colour_2, int colour_3, int colour_4)
    {
        return (get(colour_4) << 24) + (get(colour_3) << 16) + (get(colour_2) << 8) + (get(colour_1));
    }

    private static int get(int colour)
    {
        if(colour < 0) return 255;
        int red = colour/100    %10;
        int green = colour/10   %10;
        int blue = colour       %10;
        return red * 36 + green * 6 + blue;
    }
}
