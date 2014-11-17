package neresources.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import neresources.config.Settings;
import neresources.reference.Resources;
import neresources.registry.OreRegistry;
import neresources.registry.OreMatchEntry;
import neresources.utils.Font;
import neresources.utils.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.Rectangle;
import java.util.List;

public class NEIOreHandler extends TemplateRecipeHandler
{
    private static final int X_OFFSPRING = 59;
    private static final int Y_OFFSPRING = 52;
    private static final int X_AXIS_SIZE = 90;
    private static final int Y_AXIS_SIZE = 40;
    private static final int X_ITEM = 8;
    private static final int Y_ITEM = 6;

    private static int CYCLE_TIME =(int) (10 * Settings.CYCLE_TIME);

    public static void loadSettings()
    {
        CYCLE_TIME = (int) (10 * Settings.CYCLE_TIME);
    }

    private static long cycleAt = -1;

    @Override
    public String getGuiTexture()
    {
        return Resources.Gui.ORE_NEI.toString();
    }

    @Override
    public String getRecipeName()
    {
        return "Ore Gen";
    }

    @Override
    public int recipiesPerPage()
    {
        return 2;
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(60, -12, 45, 10), NEIConfig.ORE, new Object()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (outputId.equals(NEIConfig.ORE))
        {
            for (OreMatchEntry entry : OreRegistry.getInstance().getOres())
                arecipes.add(new CachedOre(entry));
            cycleAt = -1;
        } else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        OreMatchEntry entry = OreRegistry.getInstance().getRegistryMatches(result);
        if (entry != null) arecipes.add(new CachedOre(entry));
        cycleAt = -1;
    }

    @Override
    public void drawExtras(int recipe)
    {
        CachedOre cachedOre = (CachedOre) arecipes.get(recipe);
        double[] array = cachedOre.oreMatchEntry.getChances();
        double max = 0;
        for (double d : array)
            if (d > max) max = d;
        double xPrev = X_OFFSPRING;
        double yPrev = Y_OFFSPRING;
        double space = X_AXIS_SIZE / (array.length * 1D);
        int precision = array.length / 2 < 1 ? 1 : array.length / 2;
        for (int i = 0; i < array.length; i++)
        {
            double x = xPrev + space;
            int y = Y_OFFSPRING - (int) ((array[i] / max) * Y_AXIS_SIZE);
            RenderHelper.drawLine(xPrev, yPrev, x, y, cachedOre.getLineColor(), precision);
            xPrev = x;
            yPrev = y;
        }

        Font font = new Font(true);
        font.print("0%", X_OFFSPRING - 10, Y_OFFSPRING - 7);
        font.print(String.format("%.2f", max * 100) + "%", X_OFFSPRING - 20, Y_OFFSPRING - Y_AXIS_SIZE);
        int minY = cachedOre.oreMatchEntry.getMinY() - Settings.EXTRA_RANGE;
        font.print(minY < 0 ? 0 : minY, X_OFFSPRING - 3, Y_OFFSPRING + 2);
        int maxY = cachedOre.oreMatchEntry.getMaxY() + Settings.EXTRA_RANGE;
        font.print(maxY > 255 ? 255 : maxY, X_OFFSPRING + X_AXIS_SIZE, Y_OFFSPRING + 2);
        font.print("bestY: " + cachedOre.oreMatchEntry.getBestY(), X_ITEM - 2, Y_ITEM + 20);

        cachedOre.cycleItemStack(cycleticks);
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe)
    {
        if (((CachedOre)arecipes.get(recipe)).oreMatchEntry.isSilkTouchNeeded(stack))
            currenttip.add("§3SilkTouch Needed");
        return currenttip;
    }

    public class CachedOre extends TemplateRecipeHandler.CachedRecipe
    {
        private OreMatchEntry oreMatchEntry;
        private int i;

        public CachedOre(OreMatchEntry oreMatchEntry)
        {
            this.oreMatchEntry = oreMatchEntry;
            i = 0;
        }

        @Override
        public PositionedStack getResult()
        {
            return new PositionedStack(oreMatchEntry.getOres()[i], X_ITEM, Y_ITEM);
        }

        public int getLineColor()
        {
            return this.oreMatchEntry.getColour();
        }

        public void cycleItemStack(long tick)
        {
            if (cycleAt == -1) cycleAt = tick + CYCLE_TIME;

            if (tick >= cycleAt)
            {
                if (++i >= oreMatchEntry.getOres().length) i = 0;
                cycleAt += CYCLE_TIME;
            }
        }
    }
}