package com.envyful.better.dex.rewards.forge.ui;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.better.dex.rewards.forge.BetterDexRewards;
import com.envyful.better.dex.rewards.forge.config.BetterDexRewardsConfig;
import com.envyful.better.dex.rewards.forge.player.DexRewardsAttribute;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

public class DexRewardsMainUI {

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        if (BetterDexRewards.getInstance().getConfig() == null) {
            FMLCommonHandler.instance().getFMLLogger().error("CONFIG DID NOT LOAD CORRECTLY");
            return;
        }

        ConfigInterface config = BetterDexRewards.getInstance().getConfig().getConfigInterface();
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(3)
                .build();


        for (ConfigItem fillerItem : config.getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(new ItemBuilder()
                            .type(Item.getByNameOrId(fillerItem.getType()))
                            .name(fillerItem.getName())
                            .lore(fillerItem.getLore())
                            .damage(fillerItem.getDamage())
                            .amount(fillerItem.getAmount())
                            .build())
                    .build());
        }

        DexRewardsAttribute attribute = player.getAttribute(BetterDexRewards.class);

        if (attribute == null) {
            return;
        }

        double percentage = attribute.getPokeDexPercentage();
        BetterDexRewardsConfig actualConfig = BetterDexRewards.getInstance().getConfig();

        if (actualConfig.getPercentageItem().isEnabled()) {
            pane.set(actualConfig.getPercentageItem().getXPos(), actualConfig.getPercentageItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(actualConfig.getPercentageItem()))
                                                .lore(getLore(percentage, actualConfig.getPercentageItem())).build())
                             .build()
            );
        }

        if (actualConfig.getRanksItem().isEnabled()) {
            pane.set(actualConfig.getRanksItem().getXPos(), actualConfig.getRanksItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(actualConfig.getRanksItem()))
                             .clickHandler((envyPlayer, clickType) -> BetterDexRewardsUI.open((EnvyPlayer<EntityPlayerMP>) envyPlayer))
                             .build()
            );
        }

        if (actualConfig.getMissingItem().isEnabled()) {
            pane.set(actualConfig.getMissingItem().getXPos(), actualConfig.getMissingItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(actualConfig.getMissingItem()))
                             .clickHandler((envyPlayer, clickType) -> DexRewardsMissingUI.open((EnvyPlayer<EntityPlayerMP>) envyPlayer))
                             .build()
            );
        }

        PositionableConfigItem infoItem = BetterDexRewards.getInstance().getConfig().getInfoItem();

        if (infoItem.isEnabled()) {
            pane.set(infoItem.getXPos(), infoItem.getYPos(), GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(UtilConfigItem.fromConfigItem(infoItem))
                    .build());
        }

        GuiFactory.guiBuilder()
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .setPlayerManager(BetterDexRewards.getInstance().getPlayerManager())
                .height(3)
                .title(UtilChatColour.translateColourCodes('&', config.getTitle()))
                .build().open(player);
    }

    private static List<String> getLore(double percentage, ConfigItem item) {
        List<String> lore = Lists.newArrayList();

        for (String s : item.getLore()) {
            lore.add(UtilChatColour.translateColourCodes('&', s.replace(
                    "%percentage%",
                    String.format("%.2f", percentage) + "%"
            )));
        }

        return lore;
    }
}
