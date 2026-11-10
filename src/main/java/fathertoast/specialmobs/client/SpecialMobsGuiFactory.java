package fathertoast.specialmobs.client;

import fathertoast.specialmobs.SpecialMobsMod;
import fathertoast.specialmobs.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Makes this mod's options editable from the mod list. The screen shows the categories of the config file as they
 * are; when it is closed, Forge announces the change and {@link ClientProxy} has the options applied.
 */
public class SpecialMobsGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraft) { }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        Configuration file = Config.file();
        List<IConfigElement> categories = new ArrayList<>();
        for (String category : Config.get().categoryNames()) {
            categories.add(new ConfigElement(file.getCategory(category)));
        }
        return new GuiConfig(parentScreen, categories, SpecialMobsMod.MOD_ID, false, false, SpecialMobsMod.NAME,
            GuiConfig.getAbridgedConfigPath(file.toString())
        );
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Set.of();
    }

}
