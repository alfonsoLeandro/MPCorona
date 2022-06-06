package com.github.alfonsoleandro.corona.managers;

import com.github.alfonsoleandro.corona.Corona;
import com.github.alfonsoleandro.mputils.itemstacks.MPItemStacks;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

public class RecipesManager extends Reloadable {

    private final Corona plugin;
    private final Settings settings;
    private final NamespacedKey potionRecipeKey;
    private final NamespacedKey maskRecipeKey;
    private ItemStack maskItem;
    private ItemStack curePotionItem;

    public RecipesManager(Corona plugin) {
        super(plugin);
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.potionRecipeKey = new NamespacedKey(plugin, "CurePotion");
        this.maskRecipeKey = new NamespacedKey(plugin, "FaceMask");
        setMaskItem();
        setCurePotionItem();
        registerMaskRecipe();
        registerPotionRecipe();
    }

    public void registerPotionRecipe() {
        if(this.settings.isCurePotionDisabled() || this.settings.isCurePotionRecipeDisabled()) {
            return;
        }
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();
        String path = "config.cure potion";

        ShapedRecipe potionRecipe = new ShapedRecipe(this.potionRecipeKey, this.curePotionItem);
        potionRecipe.shape("ABC","DEF","GHI");
        potionRecipe.setIngredient('A', Material.valueOf(config.getString(path+".recipe.A")));
        potionRecipe.setIngredient('B', Material.valueOf(config.getString(path+".recipe.B")));
        potionRecipe.setIngredient('C', Material.valueOf(config.getString(path+".recipe.C")));
        potionRecipe.setIngredient('D', Material.valueOf(config.getString(path+".recipe.D")));
        potionRecipe.setIngredient('E', Material.valueOf(config.getString(path+".recipe.E")));
        potionRecipe.setIngredient('F', Material.valueOf(config.getString(path+".recipe.F")));
        potionRecipe.setIngredient('G', Material.valueOf(config.getString(path+".recipe.G")));
        potionRecipe.setIngredient('H', Material.valueOf(config.getString(path+".recipe.H")));
        potionRecipe.setIngredient('I', Material.valueOf(config.getString(path+".recipe.I")));

        Bukkit.addRecipe(potionRecipe);
    }


    public void registerMaskRecipe() {
        if(this.settings.isMaskDisabled() || this.settings.isMaskRecipeDisabled()) {
            return;
        }
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();
        String path = "config.mask";

        ShapedRecipe maskRecipe = new ShapedRecipe(this.maskRecipeKey, this.maskItem);
        maskRecipe.shape("ABC","DEF","GHI");
        maskRecipe.setIngredient('A', Material.valueOf(config.getString(path+".recipe.A")));
        maskRecipe.setIngredient('B', Material.valueOf(config.getString(path+".recipe.B")));
        maskRecipe.setIngredient('C', Material.valueOf(config.getString(path+".recipe.C")));
        maskRecipe.setIngredient('D', Material.valueOf(config.getString(path+".recipe.D")));
        maskRecipe.setIngredient('E', Material.valueOf(config.getString(path+".recipe.E")));
        maskRecipe.setIngredient('F', Material.valueOf(config.getString(path+".recipe.F")));
        maskRecipe.setIngredient('G', Material.valueOf(config.getString(path+".recipe.G")));
        maskRecipe.setIngredient('H', Material.valueOf(config.getString(path+".recipe.H")));
        maskRecipe.setIngredient('I', Material.valueOf(config.getString(path+".recipe.I")));

        Bukkit.addRecipe(maskRecipe);
    }

    public void setMaskItem() {
        this.maskItem = MPItemStacks.newItemStack(Material.PLAYER_HEAD,
                1,
                this.settings.getMaskItemName(),
                this.settings.getMaskItemLore());
        SkullMeta skull = (SkullMeta) this.maskItem.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}",
                this.settings.getMaskSkinURL()).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            assert skull != null;
            profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        this.maskItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
    }

    public void setCurePotionItem(){
        ItemStack potion = new ItemStack(Material.POTION);
        this.curePotionItem = MPItemStacks.newItemStack(Material.POTION,
                1,
                this.settings.getCurePotionItemName(),
                this.settings.getCurePotionItemLore());
        ItemMeta meta = potion.getItemMeta();
        assert meta != null;
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4), true);
        ((PotionMeta)meta).addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 4), true);

        potion.setItemMeta(meta);
    }


    public void unregisterRecipes(){
        Bukkit.getServer().removeRecipe(this.potionRecipeKey);
        Bukkit.getServer().removeRecipe(this.maskRecipeKey);
    }


    public ItemStack getMaskItem() {
        return this.maskItem;
    }

    public ItemStack getCurePotionItem() {
        return this.curePotionItem;
    }

    @Override
    public void reload(boolean deep) {
        unregisterRecipes();
        setMaskItem();
        setCurePotionItem();
        registerMaskRecipe();
        registerPotionRecipe();
    }
}
